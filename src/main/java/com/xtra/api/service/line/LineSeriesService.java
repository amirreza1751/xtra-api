package com.xtra.api.service.line;

import com.querydsl.jpa.JPAExpressions;
import com.xtra.api.mapper.line.LineSeriesMapper;
import com.xtra.api.model.category.QCategoryVod;
import com.xtra.api.model.vod.QSeries;
import com.xtra.api.model.vod.Series;
import com.xtra.api.projection.line.movie.MoviePlayListView;
import com.xtra.api.projection.line.series.SeriesPlayListView;
import com.xtra.api.projection.line.series.SeriesPlayView;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.service.CrudService;
import com.xtra.api.util.OptionalBooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class LineSeriesService extends CrudService<Series, Long, SeriesRepository> {
    private final LineSeriesMapper seriesMapper;
    private final QSeries series = QSeries.series;
    private final QCategoryVod categoryVod = QCategoryVod.categoryVod;

    public LineSeriesService(SeriesRepository repository, LineSeriesMapper seriesMapper) {
        super(repository, "Series");
        this.seriesMapper = seriesMapper;
    }

    public Page<SeriesPlayListView> getSeriesPlaylist(int pageNo, String search, String sortBy, Long categoryId) {
        var expression = new OptionalBooleanBuilder(series.isNotNull())
                .notNullAnd(series.name::contains, search)
                .build();
        if (categoryId != null) {
            expression = expression.and(series.categories.contains(JPAExpressions.selectFrom(categoryVod).where(categoryVod.category.id.eq(categoryId))));
        }
        return repository.findAll(expression, getSortingPageable(pageNo, 50, sortBy, "desc")).map(seriesMapper::convertToPlaylist);
    }

    public SeriesPlayView getSeries(Long id) {
        return seriesMapper.convertToPlayView(findByIdOrFail(id));
    }

    public List<SeriesPlayListView> getLast10SeriesPlaylist() {
        return repository.findTop10ByOrderByCreatedDateDesc().stream().map(seriesMapper::convertToPlaylist).collect(Collectors.toList());
    }

    @Override
    protected Page<Series> findWithSearch(String search, Pageable page) {
        return null;
    }
}
