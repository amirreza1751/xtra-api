package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.MovieMapper;
import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.setting.Settings;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.movie.*;
import com.xtra.api.repository.MovieRepository;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.repository.filter.MovieFilter;
import com.xtra.api.util.OptionalBooleanBuilder;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class MovieService extends VodService<Movie, MovieRepository> {

    private final ServerService serverService;
    private final VideoRepository videoRepository;
    private final VideoService videoService;
    private final SettingService settingService;
    private final MovieMapper movieMapper;
    private final QMovie movie = QMovie.movie;

    @Autowired
    protected MovieService(MovieRepository repository, ServerService serverService, VideoRepository videoRepository, VideoService videoService, SettingService settingService, MovieMapper movieMapper) {
        super(repository);
        this.serverService = serverService;
        this.videoRepository = videoRepository;
        this.videoService = videoService;
        this.settingService = settingService;
        this.movieMapper = movieMapper;
    }

    @Override
    public Page<Movie> findWithSearch(String search, Pageable pageable) {
        return repository.findAllByNameContainsOrInfoPlotContainsOrInfoCastContainsOrInfoDirectorContainsOrInfoGenresContainsOrInfoCountryContains(search, search, search, search, search, search, pageable);
    }

    public Page<MovieListView> getAll(int pageNo, int pageSize, String sortBy, String sortDir, MovieFilter filter) {
        var predicate = new OptionalBooleanBuilder(movie.isNotNull())
                .notNullAnd(movie.name::contains, filter.getName())
                .build();
        var search = filter.getSearch();
        if (search != null) {
            predicate = predicate.andAnyOf(
                    movie.name.containsIgnoreCase(search)
            );
        }
        return repository.findAll(predicate, getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(movieMapper::convertToListView);
    }

    public MovieView getViewById(Long id) {
        return movieMapper.convertToView(findByIdOrFail(id));
    }

    public MovieView add(MovieInsertView insertView, boolean encode) {
        return movieMapper.convertToView(insert(movieMapper.convertToEntity(insertView), encode));
    }

    public Movie insert(Movie movie, boolean encode) {
        for (Video video : movie.getVideos()) {
            this.generateToken(video);
        }

        videoService.updateVideoInfo(movie.getVideos());
        var savedEntity = repository.save(movie);
        if (encode) {
            var video = movie.getVideos().stream().findFirst().get();
            serverService.sendEncodeRequest(video.getVideoServers().stream().findFirst().get().getServer(), video);
        }
        return savedEntity;
    }

    public void saveAll(MovieBatchUpdateView movieBatchUpdateView, boolean encode) {
        var movieIds = movieBatchUpdateView.getMovieIds();
        var serverIds = movieBatchUpdateView.getServerIds();
        var collectionIds = movieBatchUpdateView.getCollectionIds();

        if (movieIds != null) {
            for (Long movieId : movieIds) {
                var movie = repository.findById(movieId).orElseThrow(() -> new EntityNotFoundException("Movie", movieId.toString()));

                if (collectionIds.size() > 0) {
                    Set<CollectionVod> collectionMovieSet = movieMapper.convertToCollections(collectionIds, movie);
                    if (!movieBatchUpdateView.getKeepCollections())
                        movie.getCollectionAssigns().retainAll(collectionMovieSet);
                    movie.getCollectionAssigns().addAll(collectionMovieSet);
                }

                if (serverIds.size() > 0) {
                    movie.getVideos().forEach(video -> {
                        Set<VideoServer> videoServers = movieMapper.convertToVideoServers(serverIds, movie);

                        if (!movieBatchUpdateView.getKeepServers())
                            video.getVideoServers().retainAll(videoServers);
                        video.getVideoServers().addAll(videoServers);
                    });
                }

                if (encode) {
                    var video = movie.getVideos().stream().findFirst().get();
                    serverService.sendEncodeRequest(video.getVideoServers().stream().findFirst().get().getServer(), video);
                }

                repository.save(movie);
            }
        }
    }

    public void deleteAll(MovieBatchDeleteView movieView) {
        var movieIds = movieView.getMovieIds();
        if (movieIds != null) {
            for (Long movieId : movieIds) {
                deleteOrFail(movieId);
            }
        }
    }

    public void importMovies(MovieImportView importView, Boolean encode) {
        var servers = importView.getServers();
        var collections = importView.getCollections();
        var movies = importView.getMovies();

        for (MovieImport movie : movies) {
            MovieInsertView insertView = new MovieInsertView();
            insertView.setCollections(collections);
            insertView.setServers(servers);
            insertView.setName(movie.getName());
            insertView.setInfo(getMovieInfo(movie.getTmdbId()));
            insertView.setVideos(movie.getVideos());
            insert(movieMapper.convertToEntity(insertView), encode);
        }
    }

    public MovieInfoInsertView getMovieInfo(int tmdbId) {
        String tmdb_apikey = this.settingService.getSettingValue(Settings.TMDB_APIKEY);
        TmdbMovies movies = new TmdbApi(tmdb_apikey).getMovies();
        MovieDb movieInfo = movies.getMovie(tmdbId, "en", TmdbMovies.MovieMethod.credits, TmdbMovies.MovieMethod.videos);
        Credits credits = movieInfo.getCredits();
        List<info.movito.themoviedbapi.model.Video> videos = movieInfo.getVideos();
        MovieInfoInsertView view = new MovieInfoInsertView();
        view.setTmdbId(tmdbId);
        view.setPosterPath("https://image.tmdb.org/t/p/w600_and_h900_bestv2" + movieInfo.getPosterPath());
        view.setBackdropPath("https://image.tmdb.org/t/p/w1280" + movieInfo.getBackdropPath());
        view.setPlot(movieInfo.getOverview());
        StringBuilder casts = new StringBuilder();
        var castCount = 0;
        for (PersonCast cast : movieInfo.getCast()) {
            castCount += 1;
            if (castCount <= 5) {
                if (casts.length() > 0) {
                    casts.append(", ");
                }
                casts.append(cast.getName());
            }
        }
        view.setCast(casts.toString());

        var director = "";
        for (PersonCrew crew : credits.getCrew()) {
            if (crew.getDepartment().equals("Directing")) {
                director = crew.getName();
            }
        }
        view.setDirector(director);

        StringBuilder genres = new StringBuilder();
        var genresCount = 0;
        for (Genre genre : movieInfo.getGenres()) {
            genresCount += 1;
            if (genresCount <= 3) {
                if (genres.length() > 0) {
                    genres.append(", ");
                }
                genres.append(genre.getName());
            }
        }
        view.setGenres(genres.toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(movieInfo.getReleaseDate(), formatter);
        view.setReleaseDate(localDate);

        view.setRuntime(movieInfo.getRuntime());

        var youtubeTrailer = "";
        for (info.movito.themoviedbapi.model.Video video : videos) {
            if (video.getType().equals("Trailer") && video.getSite().equals("YouTube"))
                youtubeTrailer = "https://www.youtube.com/watch?v=" + video.getKey();
        }
        view.setYoutubeTrailer(youtubeTrailer);

        view.setRating(movieInfo.getUserRating());
        if (movieInfo.getProductionCountries().size() > 0)
            view.setCountry(movieInfo.getProductionCountries().get(0).getName());


        return view;
    }

    public List<Subtitle> updateSubtitles(Long id, Long vidId, List<Subtitle> subtitles) {
        if (subtitles.size() == 0)
            throw new RuntimeException("provide at least one subtitle");

        Movie movie = findByIdOrFail(id);
        var vid = movie.getVideos().stream().filter(video -> video.getId().equals(vidId)).findAny();
        var video = vid.orElseThrow(() -> new EntityNotFoundException("Video", vidId.toString()));
        video.setSubtitles(subtitles);
        videoRepository.save(video);
        return video.getSubtitles();
    }

    public List<Audio> updateAudios(Long id, Long vidId, List<Audio> audios) {
        if (audios.size() == 0)
            throw new RuntimeException("provide at least one audio");

        Movie movie = findByIdOrFail(id);
        var vid = movie.getVideos().stream().filter(video -> video.getId().equals(vidId)).findAny();
        var video = vid.orElseThrow(() -> new EntityNotFoundException("Video", vidId.toString()));
        video.setAudios(audios);
        var result = serverService.SetAudioRequest(movie);
        video.setLocation(result);
        videoRepository.save(video);
        return video.getAudios();
    }

    public void encode(Long id) {
        var movie = findByIdOrFail(id);
        movie.getVideos().forEach(video -> {
            videoService.encode(video.getId());
        });
    }

    public MovieView save(Long id, MovieInsertView movieInsertView, boolean encode) {
        return movieMapper.convertToView(updateOrFail(id, movieMapper.convertToEntity(movieInsertView), encode));
    }

    public Movie updateOrFail(Long id, Movie newMovie, boolean encode) {
        var oldMovie = findByIdOrFail(id);
        copyProperties(newMovie, oldMovie, "id", "collectionAssigns", "videos", "servers", "categories");

        //remove old collections from Movie and add new collections
        if (newMovie.getCollectionAssigns() != null) {
            oldMovie.getCollectionAssigns().clear();
            oldMovie.getCollectionAssigns().addAll(newMovie.getCollectionAssigns().stream().peek(collectionVod -> {
                collectionVod.setId(new CollectionVodId(collectionVod.getCollection().getId(), oldMovie.getId()));
                collectionVod.setVod(oldMovie);
            }).collect(Collectors.toSet()));
        }

        oldMovie.getVideos().retainAll(newMovie.getVideos());
        List<Video> videosToAdd = new ArrayList<>();
        for (Video video : newMovie.getVideos()) {
            var target = oldMovie.getVideos().stream().filter(videoItem -> videoItem.equals(video)).findFirst();
            if (target.isPresent()) {
                copyProperties(video, target, "id", "token", "encodeStatus", "videoInfo", "videoServers");
                target.get().getAudios().clear();
                target.get().getAudios().addAll(video.getAudios());
                target.get().getSubtitles().clear();
                target.get().getSubtitles().addAll(video.getSubtitles());
            } else {
                videosToAdd.add(video);
            }
        }
        for (Video video : videosToAdd) {
            video.setId(null);
            this.generateToken(video);
        }
        oldMovie.getVideos().addAll(videosToAdd);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            videoService.updateVideoInfo(oldMovie.getVideos());
            repository.save(oldMovie);
        });
        executor.shutdown();
        if (encode)
            encode(id);
        return repository.save(oldMovie);
    }

    public void updateEncodeStatus(Long id, Long vidId, Map<String, String> encodeResult) {
        var movie = findByIdOrFail(id);
        var vid = movie.getVideos().stream().filter(video -> video.getId().equals(vidId)).findAny();
        var video = vid.orElseThrow(() -> new EntityNotFoundException("Video", vidId.toString()));
        video.setEncodeStatus(EncodeStatus.valueOf(encodeResult.get("encodeStatus")));
        video.setLocation(encodeResult.get("location"));
        videoRepository.save(video);
    }

    public void updateMediaInfo(Long id, Long vidId, VideoInfo videoInfo) {
        var movie = findByIdOrFail(id);
        var vid = movie.getVideos().stream().filter(video -> video.getId().equals(vidId)).findAny();
        var video = vid.orElseThrow(() -> new EntityNotFoundException("Video", vidId.toString()));
        video.setVideoInfo(videoInfo);
        videoRepository.save(video);
    }

    public void generateToken(Video video) {
        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (videoRepository.findByToken(token).isPresent());
        video.setToken(token);
        video.setEncodeStatus(EncodeStatus.NOT_ENCODED);

    }


}
