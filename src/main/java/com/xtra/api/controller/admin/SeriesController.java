package com.xtra.api.controller.admin;

import com.xtra.api.model.Series;
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

}
