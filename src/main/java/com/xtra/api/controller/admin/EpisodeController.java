package com.xtra.api.controller.admin;

import com.xtra.api.model.Series;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.episode.EpisodeListView;
import com.xtra.api.projection.admin.episode.EpisodeView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.service.admin.EpisodeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/episodes")
public class EpisodeController {
    final private EpisodeService episodeService;

    public EpisodeController(EpisodeService episodeService) {
        this.episodeService = episodeService;
    }

    @GetMapping("")
    public ResponseEntity<Page<EpisodeListView>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(episodeService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpisodeView> getEpisodeById(@PathVariable Long id) {
        return ResponseEntity.ok(episodeService.getViewById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Series> editEpisode(@PathVariable Long id, @RequestBody EpisodeInsertView episodeInsertView) {
        return ResponseEntity.ok(episodeService.editEpisode(id, episodeInsertView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Series> deleteEpisode(@PathVariable Long id) {
        episodeService.deleteEpisode(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
