package com.xtra.api.controller.admin;

import com.xtra.api.model.*;
import com.xtra.api.projection.admin.channel.ChannelBatchDeleteView;
import com.xtra.api.projection.admin.channel.ChannelBatchInsertView;
import com.xtra.api.projection.admin.movie.MovieBatchUpdateView;
import com.xtra.api.projection.admin.movie.MovieInsertView;
import com.xtra.api.projection.admin.movie.MovieView;
import com.xtra.api.service.admin.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<MovieView> addMovie(@RequestBody MovieInsertView movie, @PathVariable(required = false) boolean encode) {
        return ResponseEntity.ok(movieService.add(movie, encode));
    }

    @PatchMapping(value = {"/{id}", "{id}/{encode}"})
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie, @PathVariable(required = false) boolean encode) {
        return ResponseEntity.ok(movieService.updateOrFail(id, movie, encode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Batch Actions
    @PatchMapping(value = {"/batch", "/batch/{encode}"})
    public ResponseEntity<?> updateMovies(@RequestBody MovieBatchUpdateView movieView, @PathVariable(required = false) boolean encode) {
        movieService.saveAll(movieView, encode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteChannels(@RequestBody ChannelBatchDeleteView channelView) {
//        channelService.deleteAll(channelView);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/token/{vod_token}/id")
    public ResponseEntity<Video> getVodIdByToken(@PathVariable("vod_token") String vodToken) {
        return ResponseEntity.ok(movieService.getByToken(vodToken));
    }

    @GetMapping("/{id}/encode")
    public ResponseEntity<Movie> encodeMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.encode(id));
    }

    @PatchMapping("/{id}/videos/{vidId}/subtitles")
    public ResponseEntity<List<Subtitle>> updateSubtitles(@PathVariable Long id, @PathVariable Long vidId, @RequestBody List<Subtitle> subtitles) {
        return ResponseEntity.ok(movieService.updateSubtitles(id, vidId, subtitles));
    }

    @PatchMapping("{id}/videos/{vidId}/audios")
    public ResponseEntity<List<Audio>> updateAudios(@PathVariable Long id, @PathVariable Long vidId, @RequestBody List<Audio> audios) {
        return ResponseEntity.ok(movieService.updateAudios(id, vidId, audios));
    }

    @PatchMapping("/{id}/videos/{vidId}/encode_status")
    public ResponseEntity<?> setEncodeStatus(@PathVariable Long id, @PathVariable Long vidId, @RequestBody Map<String, String> encodeResult) {
        movieService.updateEncodeStatus(id, vidId, encodeResult);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/videos/{vidId}/media_info")
    public ResponseEntity<?> setMediaInfo(@PathVariable Long id, @PathVariable Long vidId, @RequestBody VideoInfo mediaInfo) {
        movieService.updateMediaInfo(id, vidId, mediaInfo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
