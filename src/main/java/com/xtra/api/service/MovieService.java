package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.repository.MovieRepository;
import com.xtra.api.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.xtra.api.util.Utilities.generateRandomString;

@Service
public class MovieService extends VodService<Movie, MovieRepository> {

    private final ServerService serverService;
    private final VideoRepository videoRepository;

    @Autowired
    protected MovieService(MovieRepository repository, ServerService serverService, VideoRepository videoRepository) {
        super(repository, Movie.class);
        this.serverService = serverService;
        this.videoRepository = videoRepository;
    }

    @Override
    public Page<Movie> findWithSearch(Pageable pageable, String search) {
        return repository.findByNameLikeOrInfoPlotLikeOrInfoCastLikeOrInfoDirectorLikeOrInfoGenresLikeOrInfoCountryLike(search, search, search, search, search, search, pageable);
    }

    public Movie add(Movie movie, boolean encode) {
        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (repository.findByToken(token).isPresent());
        movie.setToken(token);

        if (encode) {
            serverService.sendEncodeRequest(movie);
        }
        return super.insert(movie);
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
        serverService.sendEncodeRequest(movie);
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
