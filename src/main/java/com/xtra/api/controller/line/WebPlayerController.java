package com.xtra.api.controller.line;

import com.xtra.api.model.MediaType;
import com.xtra.api.projection.line.CategoryView;
import com.xtra.api.projection.line.channel.ChannelPlayListView;
import com.xtra.api.projection.line.channel.ChannelPlayView;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.movie.MoviePlayView;
import com.xtra.api.projection.line.series.EpisodePlayView;
import com.xtra.api.projection.line.series.SeriesPlayListView;
import com.xtra.api.projection.line.series.SeriesPlayView;
import com.xtra.api.service.line.LineCategoriesService;
import com.xtra.api.service.line.LineChannelService;
import com.xtra.api.service.line.LineMovieService;
import com.xtra.api.service.line.LineSeriesService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("webplayer")
@PreAuthorize("hasAnyRole({'LINE'})")
public class WebPlayerController {
    private final LineMovieService lineMovieService;
    private final LineChannelService lineChannelService;
    private final LineSeriesService lineSeriesService;
    private final LineCategoriesService lineCategoriesService;

    public WebPlayerController(LineMovieService lineMovieService, LineChannelService lineChannelService, LineSeriesService lineSeriesService, LineCategoriesService lineCategoriesService) {
        this.lineMovieService = lineMovieService;
        this.lineChannelService = lineChannelService;
        this.lineSeriesService = lineSeriesService;
        this.lineCategoriesService = lineCategoriesService;
    }

    @GetMapping("/movies")
    public ResponseEntity<Page<MoviePlayListView>> getMoviesPlaylist(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(lineMovieService.getMoviesPlaylist(pageNo, search, sortBy, categoryId));
    }

    @GetMapping("/movies/last10")
    public ResponseEntity<List<MoviePlayListView>> getLast10MoviesPlaylist() {
        return ResponseEntity.ok(lineMovieService.getLast10MoviesPlaylist());
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<MoviePlayView> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(lineMovieService.getMovie(id));
    }

    @GetMapping("/channels")
    public ResponseEntity<Page<ChannelPlayListView>> getChannelPlaylist(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(lineChannelService.getChannelPlaylist(pageNo, search, sortBy, categoryId));
    }

    @GetMapping("/channels/{id}")
    public ResponseEntity<ChannelPlayView> getChannel(@PathVariable Long id) {
        return ResponseEntity.ok(lineChannelService.getChannel(id));
    }


    @GetMapping("/channels/hot10")
    public ResponseEntity<List<ChannelPlayListView>> getHot10ChannelsPlaylist() {
        return ResponseEntity.ok(lineChannelService.getHot10ChannelsPlaylist());
    }

    @GetMapping("/series")
    public ResponseEntity<Page<SeriesPlayListView>> getSeriesPlaylist(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(lineSeriesService.getSeriesPlaylist(pageNo, search, sortBy, categoryId));
    }

    @GetMapping("/series/last10")
    public ResponseEntity<List<SeriesPlayListView>> getLast10SeriesPlaylist() {
        return ResponseEntity.ok(lineSeriesService.getLast10SeriesPlaylist());
    }

    @GetMapping("/series/{id}")
    public ResponseEntity<SeriesPlayView> getSeries(@PathVariable Long id) {
        return ResponseEntity.ok(lineSeriesService.getSeries(id));
    }

    @GetMapping("/episode/{id}")
    public ResponseEntity<EpisodePlayView> getEpisodeById(@PathVariable Long id) {
        return ResponseEntity.ok(lineSeriesService.getEpisodeById(id));
    }

    @GetMapping("/series/{serie_id}/season/{season_number}/episode/{episode_number}")
    public ResponseEntity<EpisodePlayView> getEpisode(@PathVariable Long serie_id, @PathVariable int season_number, @PathVariable int episode_number) {
        return ResponseEntity.ok(lineSeriesService.getEpisode(serie_id, season_number, episode_number));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryView>> getCategories(@RequestParam MediaType type) {
        return ResponseEntity.ok(lineCategoriesService.getCategories(type));
    }

}
