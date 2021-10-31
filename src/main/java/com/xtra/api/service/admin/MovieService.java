package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.MovieMapper;
import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.setting.Settings;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.movie.*;
import com.xtra.api.projection.admin.video.AudioDetails;
import com.xtra.api.projection.admin.video.EncodeRequest;
import com.xtra.api.repository.CollectionRepository;
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
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class MovieService extends VodService<Movie, MovieRepository> {

    private final ServerService serverService;
    private final SettingService settingService;
    private final MovieMapper movieMapper;
    private final CollectionRepository collectionRepository;
    private final QMovie movie = QMovie.movie;

    @Autowired
    protected MovieService(MovieRepository repository, ServerService serverService, VideoRepository videoRepository, SettingService settingService, MovieMapper movieMapper, CollectionRepository collectionRepository) {
        super(repository, videoRepository, "Movie");
        this.serverService = serverService;
        this.settingService = settingService;
        this.movieMapper = movieMapper;
        this.collectionRepository = collectionRepository;
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
        var video = movie.getVideo();
        generateVideoToken(video);
        updateVideoInfo(movie);
        var savedEntity = repository.save(movie);
        if (encode) {
            encode(movie);
        }
        return savedEntity;
    }

    public MovieView update(Long id, MovieInsertView movieInsertView, boolean encode) {
        var existingMovie = findByIdOrFail(id);
        existingMovie = movieMapper.updateEntityFromInsertView(movieInsertView, existingMovie);
        //todo update only if changed
        updateVideoInfo(existingMovie);
        var result = repository.save(existingMovie);
        if (encode)
            encode(existingMovie);
        return movieMapper.convertToView(result);

    }

    private void updateVideoInfo(Movie movie) {
        var video = movie.getVideo();
        var server = video.getVideoServers().stream().findFirst().orElseThrow(() -> new RuntimeException("at least one server is required"));
        var infoView = serverService.getMediaInfo(server.getServer(), video.getSourceLocation());
        movie.getVideo().setSourceVideoInfo(movieMapper.toVideoInfo(infoView));
    }

    public void encode(Movie movie) {
        var video = movie.getVideo();
        var servers = video.getVideoServers().stream().map(VideoServer::getServer).collect(Collectors.toList());
        var encodeRequest = createEncodeRequest(video);
        for (var server : servers) {
            serverService.sendEncodeRequest(server, encodeRequest);
        }
    }

    public void encode(Long id) {
        var movie = findByIdOrFail(id);
        encode(movie);
    }

    public void saveAll(MovieBatchUpdateView batchUpdateView, boolean encode) {
        var movies = repository.findAllById(batchUpdateView.getMovies());
        for (var movie : movies) {
            var video = movie.getVideo();
            if (batchUpdateView.isAddResolutions()) {
                video.getTargetResolutions().addAll(batchUpdateView.getTargetResolutions());
            } else {
                video.setTargetResolutions(batchUpdateView.getTargetResolutions());
            }
            var collections = collectionRepository.findAllById(batchUpdateView.getCollections());
            if (!batchUpdateView.isAddCollections()) {
                movie.getCollectionAssigns().clear();
            }
            for (var collection : collections) {
                movie.getCollectionAssigns().add(new CollectionVod(collection, movie));
            }
            //@todo update servers when multiple servers are supported
        }
        var savedMovies = repository.saveAll(movies);
        if (encode) {
            batchEncode(savedMovies);
        }

    }

    public void batchEncode(List<Movie> movies) {
        var serverEncodeRequests = new ArrayListValuedHashMap<Server, EncodeRequest>();
        for (var movie : movies) {
            var video = movie.getVideo();
            video.getVideoServers().stream().map(VideoServer::getServer).forEach(server -> serverEncodeRequests.put(server, createEncodeRequest(video)));
        }
        for (var server : serverEncodeRequests.keySet()) {
            serverService.sendBatchEncodeRequest(server, serverEncodeRequests.get(server));
        }
    }

    private EncodeRequest createEncodeRequest(Video video) {
        var encodeRequest = new EncodeRequest();
        encodeRequest.setVideoId(video.getId());
        encodeRequest.setSourceLocation(video.getSourceLocation());
        encodeRequest.setSourceAudios(video.getSourceAudios().stream().map(audio -> new AudioDetails(audio.getLocation(), audio.getLanguage())).collect(Collectors.toList()));
        encodeRequest.setTargetResolutions(video.getTargetResolutions());
        encodeRequest.setTargetAudioCodec(AudioCodec.AAC);
        encodeRequest.setTargetVideoCodec(VideoCodec.H264);
        return encodeRequest;
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
        var movies = movieMapper.convertToMovieList(importView);
        for (var movie : movies) {
            movie.setInfo(movieMapper.convertToMovieInfo(getMovieInfo(movie.getInfo().getTmdbId())));
        }
        var savedMovies = repository.saveAll(movies);
        if (encode) {
            batchEncode(savedMovies);
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

}
