package com.xtra.api.service.line;

import com.xtra.api.mapper.admin.MovieMapper;
import com.xtra.api.mapper.line.LineMovieMapper;
import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.setting.Settings;
import com.xtra.api.model.vod.*;
import com.xtra.api.projection.admin.movie.*;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.movie.MoviePlayView;
import com.xtra.api.repository.MovieRepository;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.repository.filter.MovieFilter;
import com.xtra.api.service.admin.ServerService;
import com.xtra.api.service.admin.SettingService;
import com.xtra.api.service.admin.VideoService;
import com.xtra.api.service.admin.VodService;
import com.xtra.api.util.OptionalBooleanBuilder;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.generateRandomString;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class LineMovieService extends VodService<Movie, MovieRepository> {

    private final LineMovieMapper movieMapper;

    @Autowired
    protected LineMovieService(MovieRepository repository, LineMovieMapper movieMapper) {
        super(repository);
        this.movieMapper = movieMapper;
    }

    @Override
    protected Page<Movie> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Page<MoviePlayListView> getMoviesPlaylist(int pageNo, String search, String sortBy, Long categoryId) {
        return repository.findAll(getSortingPageable(pageNo, 50, sortBy, "desc")).map(movieMapper::convertToPlayListView);
    }

    public MoviePlayView getMovie(Long id) {
        return movieMapper.convertToPlayView(findByIdOrFail(id));
    }

    public List<MoviePlayListView> getLast10MoviesPlaylist() {
        return repository.findTop10ByOrderByCreatedDateDesc().stream().map(movieMapper::convertToPlayListView).collect(Collectors.toList());
    }

}
