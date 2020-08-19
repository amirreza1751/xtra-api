package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.*;
import com.xtra.api.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.xtra.api.util.Utilities.*;
import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    @GetMapping("")
    public Page<Movie> getMovies(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);

        if (search == null)
            return movieRepository.findAll(page);
        else {
            search = wrapSearchString(search);
            return movieRepository.findByNameLikeOrInfoPlotLikeOrInfoCastLikeOrInfoDirectorLikeOrInfoGenresLikeOrInfoCountryLike(search, search, search, search, search, search, page);
        }

    }

    @PostMapping(value = {"", "/{encode}"})
    public Movie addMovie(@Valid @RequestBody Movie movie, @PathVariable(required = false) boolean encode) {
        String token;
        do {
            token = generateRandomString(8, 12, false);

        } while (movieRepository.findByToken(token).isPresent());
        movie.setToken(token);

        RestTemplate restTemplate = new RestTemplate();

        if (encode) {
            var result = restTemplate.postForObject(corePath + ":" + corePort + "/vod/encode/", movie, String.class);
            movie.setLocation(result);
            movieRepository.save(movie);
        }
        var result = restTemplate.postForObject(corePath + ":" + corePort + "/vod/info/", movie, MediaInfo.class);

        movie.setMediaInfo(result);

        return movieRepository.save(movie);
    }

    @GetMapping("/encode/{id}")
    public Movie encodeMovie(@PathVariable Long id) {
        var movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("movie not found"));
        var result = new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/encode/", movie, String.class);
        movie.setLocation(result);
        movieRepository.save(movie);
        var info = new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/info/", movie, MediaInfo.class);
        movie.setMediaInfo(info);
        movieRepository.save(movie);
        return movie;
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
    }

    @PatchMapping("/{id}")
    public Movie updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        Optional<Movie> result = movieRepository.findById(id);
        if (result.isEmpty()) {
            throw new EntityNotFound();
        }
        Movie oldMovie = result.get();
        copyProperties(movie, oldMovie, "id");
        return movieRepository.save(oldMovie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }

    @GetMapping("get_id/{vod_token}")
    public Long getVodIdByToken(@PathVariable("vod_token") String vodToken) {
        var movie = movieRepository.findByToken(vodToken);
        return movie.map(Vod::getId).orElse(null);
    }

    @PostMapping("set_subtitles/{id}")
    public List<Subtitle> setSubtitles(@PathVariable Long id, @RequestBody List<Subtitle> subtitles) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));
        if (subtitles.size() == 0)
            throw new RuntimeException("provide at least one subtitle");
        movie.setSubtitles(subtitles);
        var result = new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/set_subtitles/", movie, String.class);
        movie.setLocation(result);
        movieRepository.save(movie);
        return movie.getSubtitles();
    }

    @PostMapping("set_audios/{id}")
    public List<Audio> setAudios(@PathVariable Long id, @RequestBody List<Audio> audios) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found"));
        if (audios.size() == 0)
            throw new RuntimeException("provide at least one audio");
        movie.setAudios(audios);
        var result = new RestTemplate().postForObject(corePath + ":" + corePort + "/vod/set_audios/", movie, String.class);
        movie.setLocation(result);
        movieRepository.save(movie);
        return movie.getAudios();
    }


}
