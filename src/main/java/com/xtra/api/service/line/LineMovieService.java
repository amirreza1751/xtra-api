package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineMovieMapper;
import com.xtra.api.model.vod.Movie;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.movie.MoviePlayView;
import com.xtra.api.repository.MovieRepository;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.admin.VodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class LineMovieService extends VodService<Movie, MovieRepository> {

    private final LineMovieMapper movieMapper;

    @Autowired
    protected LineMovieService(MovieRepository repository, LineMovieMapper movieMapper, VideoRepository videoRepository) {
        super(repository, videoRepository, "Movie");
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

}
