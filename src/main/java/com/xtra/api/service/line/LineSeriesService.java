package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineSeriesMapper;
import com.xtra.api.model.vod.Series;
import com.xtra.api.projection.line.series.SeriesPlayListView;
import com.xtra.api.projection.line.series.SeriesPlayView;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class LineSeriesService extends CrudService<Series, Long, SeriesRepository> {
    private final LineSeriesMapper seriesMapper;

    public LineSeriesService(SeriesRepository repository, LineSeriesMapper seriesMapper) {
        super(repository, "Series");
        this.seriesMapper = seriesMapper;
    }

    public Page<SeriesPlayListView> getSeriesPlaylist(int pageNo, String search, String sortBy, Long categoryId) {
        return repository.findAll(getSortingPageable(pageNo, 50, sortBy, "desc")).map(seriesMapper::convertToPlaylist);
    }

    public SeriesPlayView getSeries(Long id) {
        return seriesMapper.convertToPlayView(findByIdOrFail(id));
    }

    @Override
    protected Page<Series> findWithSearch(String search, Pageable page) {
        return null;
    }
}
