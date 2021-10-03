package com.xtra.api.controller.line;

import com.xtra.api.projection.line.channel.ChannelPlayListView;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.movie.MoviePlayView;
import com.xtra.api.projection.line.series.SeriesPlayListView;
import com.xtra.api.projection.line.series.SeriesPlayView;
import com.xtra.api.service.line.LineChannelService;
import com.xtra.api.service.line.LineMovieService;
import com.xtra.api.service.line.LineSeriesService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("webplayer")
@PreAuthorize("hasAnyRole({'LINE'})")
public class WebPlayerController {
    private final LineMovieService lineMovieService;
    private final LineChannelService lineChannelService;
    private final LineSeriesService lineSeriesService;

    public WebPlayerController(LineMovieService lineMovieService, LineChannelService lineChannelService, LineSeriesService lineSeriesService) {
        this.lineMovieService = lineMovieService;
        this.lineChannelService = lineChannelService;
        this.lineSeriesService = lineSeriesService;
    }

    @GetMapping("/movies")
    public ResponseEntity<Page<MoviePlayListView>> getMoviesPlaylist(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) Long categoryId
    ) {
        return ResponseEntity.ok(lineMovieService.getMoviesPlaylist(pageNo, search, sortBy, categoryId));
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<MoviePlayView> getMovie(@PathVariable(required = true) Long id) {
        return ResponseEntity.ok(lineMovieService.getMovie(id));
    }

    @GetMapping("/channels")
    public ResponseEntity<Page<ChannelPlayListView>> getChannelPlaylist(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) Long categoryId
    ) {
        return ResponseEntity.ok(lineChannelService.getChannelPlaylist(pageNo, search, sortBy, categoryId));
    }

    @GetMapping("/series")
    public ResponseEntity<Page<SeriesPlayListView>> getSeriesPlaylist(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) Long categoryId
    ) {
        return ResponseEntity.ok(lineSeriesService.getSeriesPlaylist(pageNo, search, sortBy, categoryId));
    }

    @GetMapping("/series/{id}")
    public ResponseEntity<SeriesPlayView> getSeries(@PathVariable(required = true) Long id) {
        return ResponseEntity.ok(lineSeriesService.getSeries(id));
    }

}
