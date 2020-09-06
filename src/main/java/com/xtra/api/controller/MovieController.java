package com.xtra.api.controller;

import com.xtra.api.model.Audio;
import com.xtra.api.model.Movie;
import com.xtra.api.model.Subtitle;
import com.xtra.api.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("")
    public ResponseEntity<Page<Movie>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(movieService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findByIdOrFail(id));
    }

    @PostMapping(value = {"", "/{encode}"})
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie, @PathVariable(required = false) boolean encode) {
        return ResponseEntity.ok(movieService.add(movie, encode));
    }

    @PatchMapping(value = {"/{id}", "{id}/{encode}"})
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie, @PathVariable(required = false) boolean encode) {
        return ResponseEntity.ok(movieService.updateOrFail(id, movie, encode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/token/{vod_token}/id")
    public ResponseEntity<Long> getVodIdByToken(@PathVariable("vod_token") String vodToken) {
        return ResponseEntity.ok(movieService.getByToken(vodToken));
    }

    @GetMapping("/encode/{id}")
    public ResponseEntity<Movie> encodeMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.encode(id));
    }

    @PatchMapping("/{id}/subtitles")
    public ResponseEntity<List<Subtitle>> updateSubtitles(@PathVariable Long id, @RequestBody List<Subtitle> subtitles) {
        return ResponseEntity.ok(movieService.updateSubtitles(id, subtitles));
    }

    @PatchMapping("{id}/audios")
    public ResponseEntity<List<Audio>> updateAudios(@PathVariable Long id, @RequestBody List<Audio> audios) {
        return ResponseEntity.ok(movieService.updateAudios(id, audios));
    }


}
