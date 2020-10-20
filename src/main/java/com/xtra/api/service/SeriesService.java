package com.xtra.api.service;

import com.xtra.api.model.Series;
import com.xtra.api.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SeriesService extends CrudService<Series, Long, SeriesRepository> {

    @Autowired
    protected SeriesService(SeriesRepository repository) {
        super(repository, Series.class);
    }

    @Override
    protected Page<Series> findWithSearch(Pageable page, String search) {
        return null;
    }
}
