package com.xtra.api.service.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.admin.MovieMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.movie.MovieBatchUpdateView;
import com.xtra.api.projection.admin.movie.MovieInsertView;
import com.xtra.api.projection.admin.movie.MovieView;
import com.xtra.api.repository.MovieRepository;
import com.xtra.api.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.xtra.api.util.Utilities.generateRandomString;

@Service
public class MovieService extends VodService<Movie, MovieRepository> {

    private final ServerService serverService;
    private final VideoRepository videoRepository;
    private final MovieMapper movieMapper;

    @Autowired
    protected MovieService(MovieRepository repository, ServerService serverService, VideoRepository videoRepository, MovieMapper movieMapper) {
        super(repository);
        this.serverService = serverService;
        this.videoRepository = videoRepository;
        this.movieMapper = movieMapper;
    }

    @Override
    public Page<Movie> findWithSearch(String search, Pageable pageable) {
        return repository.findByNameLikeOrInfoPlotLikeOrInfoCastLikeOrInfoDirectorLikeOrInfoGenresLikeOrInfoCountryLike(search, search, search, search, search, search, pageable);
    }

    public MovieView add(MovieInsertView insertView, boolean encode) {
        return movieMapper.convertToView(insert(movieMapper.convertToEntity(insertView), encode));
    }

    public Movie insert(Movie movie, boolean encode) {
        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (repository.findByToken(token).isPresent());
        movie.setToken(token);
        var savedEntity = repository.save(movie);

        if (encode) {
            serverService.sendEncodeRequest(movie.getVideos().stream().findFirst().get());
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
                    Set<ServerVod> serverVods = movieMapper.convertToServers(serverIds, movie);
                    if (!movieBatchUpdateView.getKeepServers())
                        movie.getServerVods().retainAll(serverVods);
                    movie.getServerVods().addAll(serverVods);
                }

                if (encode) {
                    serverService.sendEncodeRequest(movie.getVideos().stream().findFirst().get());
                }

                repository.save(movie);
            }
        }
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

    public Movie encode(Long id) {
        var movie = findByIdOrFail(id);
        serverService.sendEncodeRequest(movie.getVideos().stream().findFirst().get());
        return repository.save(movie);
    }

    public Video getByToken(String vodToken) {
        var m = repository.findByToken(vodToken);
        return m.map(movie -> movie.getVideos().stream().findFirst().get()).orElse(null);
    }

    public Movie updateOrFail(Long id, Movie movie, boolean encode) {
        var updatedMovie = updateOrFail(id, movie);
        if (encode)
            encode(id);
        return updatedMovie;
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
}
