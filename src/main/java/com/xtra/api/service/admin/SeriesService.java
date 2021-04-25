package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.SeriesMapper;
import com.xtra.api.model.CollectionStreamId;
import com.xtra.api.model.CollectionVodId;
import com.xtra.api.model.Movie;
import com.xtra.api.model.Series;
import com.xtra.api.projection.admin.movie.MovieInsertView;
import com.xtra.api.projection.admin.series.SeriesInsertView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.repository.CollectionVodRepository;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class SeriesService extends CrudService<Series, Long, SeriesRepository> {

    private final SeriesMapper seriesMapper;
    private final CollectionVodRepository collectionVodRepository;

    @Autowired
    protected SeriesService(SeriesRepository repository, SeriesMapper seriesMapper, CollectionVodRepository collectionVodRepository) {
        super(repository, "Series");
        this.seriesMapper = seriesMapper;
        this.collectionVodRepository = collectionVodRepository;
    }

    @Override
    protected Page<Series> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Series add(SeriesInsertView seriesInsertView){
        return this.insert(seriesMapper.convertToEntity(seriesInsertView));
    }

    public Series insert(Series series){
        return repository.save(series);
    }

    public SeriesView save(Long id, SeriesInsertView seriesInsertView){
        return seriesMapper.convertToView(this.update(id, seriesMapper.convertToEntity(seriesInsertView)));
    }

    public Series update(Long id, Series series) {
        var oldSeries = findByIdOrFail(id);
        copyProperties(series, oldSeries, "id", "collectionAssigns", "seasons");
        if (series.getCollectionAssigns() != null) {
            collectionVodRepository.deleteAll();
            oldSeries.getCollectionAssigns().addAll(series.getCollectionAssigns().stream().peek(collectionVod -> {
                collectionVod.setId(new CollectionVodId(collectionVod.getCollection().getId(), oldSeries.getId()));
                collectionVod.setVod(oldSeries);
            }).collect(Collectors.toSet()));
        }
        return repository.save(oldSeries);
    }
    public void deleteSeries(Long id){
        var seriesToDelete = findByIdOrFail(id);
        if (seriesToDelete.getCollectionAssigns() != null) {
            collectionVodRepository.deleteAll();
        }
        repository.delete(seriesToDelete);
    }
}
