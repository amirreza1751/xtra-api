package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.series.SeriesBatchDeleteView;
import com.xtra.api.projection.admin.series.SeriesBatchUpdateView;
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
    public ResponseEntity<Page<SeriesView>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(seriesService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeriesView> getSeries(@PathVariable Long id) {
        return ResponseEntity.ok(seriesService.getViewById(id));
    }

    @PostMapping("")
    public ResponseEntity<SeriesView> addSeries(@RequestBody SeriesInsertView series) {
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

    // Batch Actions
    @PatchMapping("/batch")
    public ResponseEntity<?> updateAll(@RequestBody SeriesBatchUpdateView seriesBatchUpdateView) {
        seriesService.updateAll(seriesBatchUpdateView);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteAll(@RequestBody SeriesBatchDeleteView seriesBatchDeleteView) {
        seriesService.deleteAll(seriesBatchDeleteView);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //Episodes
    @PostMapping("/{id}/episodes")
    public ResponseEntity<SeriesView> addEpisode(@PathVariable Long id, @RequestBody EpisodeInsertView episodeInsertView) {
        return ResponseEntity.ok(seriesService.addEpisode(id, episodeInsertView));
    }
}
