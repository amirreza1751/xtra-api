package com.xtra.api.controller.admin;

import com.xtra.api.model.Episode;
import com.xtra.api.model.Series;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.series.SeriesInsertView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.service.admin.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/series")
public class SeriesController {
    private final SeriesService seriesService;

    @Autowired
    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    @GetMapping("")
    public ResponseEntity<Page<Series>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(seriesService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Series> getSeries(@PathVariable Long id) {
        return ResponseEntity.ok(seriesService.findByIdOrFail(id));
    }

    @PostMapping("")
    public ResponseEntity<Series> addSeries(@RequestBody SeriesInsertView series) {
        return ResponseEntity.ok(seriesService.add(series));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SeriesView> updateSeries(@PathVariable Long id, @RequestBody SeriesInsertView seriesInsertView) {
        return ResponseEntity.ok(seriesService.save(id, seriesInsertView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeries(@PathVariable Long id) {
        seriesService.deleteSeries(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //Episodes
    @PostMapping("/{id}/episodes")
    public ResponseEntity<Series> addEpisode(@PathVariable Long id, @RequestBody EpisodeInsertView episodeInsertView) {
        return ResponseEntity.ok(seriesService.addEpisode(id, episodeInsertView));
    }

    @PatchMapping("/{id}/episodes/{episodeId}")
    public ResponseEntity<Series> editEpisode(@PathVariable Long id, @PathVariable Long episodeId, @RequestBody EpisodeInsertView episodeInsertView) {
        return ResponseEntity.ok(seriesService.editEpisode(id, episodeId, episodeInsertView));
    }
}