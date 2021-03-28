package com.xtra.api.service.admin;

import com.xtra.api.model.Series;
import com.xtra.api.repository.SeriesRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SeriesService extends CrudService<Series, Long, SeriesRepository> {

    @Autowired
    protected SeriesService(SeriesRepository repository) {
        super(repository, "Series");
    }

    @Override
    protected Page<Series> findWithSearch(String search, Pageable page) {
        return null;
    }
}
