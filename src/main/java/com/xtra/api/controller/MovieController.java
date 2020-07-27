package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.Movie;
import com.xtra.api.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static com.xtra.api.util.utilities.wrapSearchString;
import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;

    @Autowired
    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }


    @GetMapping("")
    public Page<Movie> getMovies(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        Pageable page;
        Sort.Order order;
        if (sortBy != null) {
            if (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                order = Sort.Order.desc(sortBy);
            else
                order = Sort.Order.asc(sortBy);
            page = PageRequest.of(pageNo, pageSize, Sort.by(order));
        } else {
            page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc("id")));
        }

        if (search == null)
            return movieRepository.findAll(page);
        else {
            search = wrapSearchString(search);
            return movieRepository.findByNameLikeOrPlotLikeOrCastLikeOrDirectorLikeOrGenresLikeOrCountryLike(search, search, search, search, search, search, page);
        }

    }

    @PostMapping("")
    public Movie addMovie(@Valid @RequestBody Movie movie) {
        return movieRepository.save(movie);
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
    }

    @PatchMapping("/{id}")
    public Movie updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        Optional<Movie> result = movieRepository.findById(id);
        if (result.isEmpty()) {
            throw new EntityNotFound();
        }
        Movie oldMovie = result.get();
        copyProperties(movie, oldMovie, "id");
        return movieRepository.save(oldMovie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieRepository.deleteById(id);
    }
}
