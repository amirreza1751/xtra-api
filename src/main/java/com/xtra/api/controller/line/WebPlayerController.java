package com.xtra.api.controller.line;

import com.xtra.api.mapper.line.LineMovieMapperImpl;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.movie.MoviePlayView;
import com.xtra.api.service.line.LineMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("webplayer")
@PreAuthorize("hasAnyRole({'LINE'})")
public class WebPlayerController {
    private final LineMovieService lineService;

    public WebPlayerController(LineMovieService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("/movies")
    public ResponseEntity<Page<MoviePlayListView>> getMoviesPlaylist(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy, @RequestParam(required = false) Long categoryId
    ) {
        return ResponseEntity.ok(lineService.getMoviesPlaylist(pageNo, search, sortBy, categoryId));
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<MoviePlayView> getMovie(@PathVariable(required = false) Long id) {
        return ResponseEntity.ok(lineService.getMovie(id));
    }


}
