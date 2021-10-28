package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.movie.*;
import com.xtra.api.projection.admin.video.EncodeResponse;
import com.xtra.api.repository.filter.MovieFilter;
import com.xtra.api.service.admin.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @GetMapping("")
    public ResponseEntity<Page<MovieListView>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false, name = "name") String name,
                                                      @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(movieService.getAll(pageNo, pageSize, sortBy, sortDir, new MovieFilter(name, search)));
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @GetMapping("/{id}")
    public ResponseEntity<MovieView> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getViewById(id));
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @PostMapping(value = {"", "/{encode}"})
    public ResponseEntity<MovieView> addMovie(@RequestBody MovieInsertView movie, @PathVariable(required = false) boolean encode) {
        return ResponseEntity.ok(movieService.add(movie, encode));
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @PostMapping(value = {"/import", "/import/{encode}"})
    public ResponseEntity<?> importMovies(@RequestBody MovieImportView movieImportView, @PathVariable(required = false) boolean encode) {
        movieService.importMovies(movieImportView, encode);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @PatchMapping(value = {"/{id}", "{id}/{encode}"})
    public ResponseEntity<MovieView> updateMovie(@PathVariable Long id, @RequestBody MovieInsertView movie, @PathVariable(required = false) boolean encode) {
        return ResponseEntity.ok(movieService.update(id, movie, encode));
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Batch Actions
    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @PatchMapping(value = {"/batch", "/batch/{encode}"})
    public ResponseEntity<?> updateMovies(@RequestBody MovieBatchUpdateView movieView, @PathVariable(required = false) boolean encode) {
        movieService.saveAll(movieView, encode);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteMovies(@RequestBody MovieBatchDeleteView movieView) {
        movieService.deleteAll(movieView);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @GetMapping("/{id}/encode")
    public ResponseEntity<?> encodeMovie(@PathVariable Long id) {
        movieService.encode(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/encode_status")
    public ResponseEntity<?> updateEncodeStatus(@PathVariable Long id, @RequestHeader(value = "server_token") String serverToken,
                                                @RequestBody EncodeResponse encodeResponse) {
        movieService.updateEncodeStatus(id, serverToken, encodeResponse);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
