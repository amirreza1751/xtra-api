package com.xtra.api.service;

import com.xtra.api.model.Audio;
import com.xtra.api.model.Movie;
import com.xtra.api.model.Subtitle;
import com.xtra.api.model.Vod;
import com.xtra.api.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.xtra.api.util.Utilities.generateRandomString;

@Service
public class MovieService extends CrudService<Movie, Long> {

    private final ServerService serverService;

    @Autowired
    protected MovieService(MovieRepository repository, ServerService serverService) {
        super(repository);
        this.serverService = serverService;
    }

    @Override
    public Page<Movie> findWithSearch(Pageable pageable, String search) {
        return ((MovieRepository) repository).findByNameLikeOrInfoPlotLikeOrInfoCastLikeOrInfoDirectorLikeOrInfoGenresLikeOrInfoCountryLike(search, search, search, search, search, search, pageable);
    }

    public Movie add(Movie movie, boolean encode) {
        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (((MovieRepository) repository).findByToken(token).isPresent());
        movie.setToken(token);

        if (encode) {
            var location = serverService.sendEncodeRequest(movie);
            movie.setLocation(location);
        }
        var result = serverService.getMediaInfo(movie);
        movie.setMediaInfo(result);
        return super.add(movie);
    }

    public List<Subtitle> updateSubtitles(Long id, List<Subtitle> subtitles) {
        if (subtitles.size() == 0)
            throw new RuntimeException("provide at least one subtitle");

        Movie movie = getByIdOrFail(id);
        movie.setSubtitles(subtitles);
        repository.save(movie);
        return movie.getSubtitles();
    }

    public List<Audio> updateAudios(Long id, List<Audio> audios) {
        if (audios.size() == 0)
            throw new RuntimeException("provide at least one audio");

        Movie movie = getByIdOrFail(id);
        movie.setAudios(audios);
        var result = serverService.SetAudioRequest(movie);
        movie.setLocation(result);
        repository.save(movie);
        return movie.getAudios();
    }

    public Movie encode(Long id) {
        var movie = getByIdOrFail(id);
        var result = serverService.sendEncodeRequest(movie);
        movie.setLocation(result);
        var info = serverService.getMediaInfo(movie);
        movie.setMediaInfo(info);
        return repository.save(movie);
    }

    public Long getByToken(String vodToken) {
        var movie = ((MovieRepository) repository).findByToken(vodToken);
        return movie.map(Vod::getId).orElse(null);
    }

    public Movie updateOrFail(Long id, Movie movie, boolean encode) {
        var updatedMovie = updateOrFail(id, movie);
        if (encode)
            encode(id);
        return updatedMovie;
    }
}
