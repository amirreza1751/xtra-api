package com.xtra.api.service;

import com.xtra.api.model.Collection;
import com.xtra.api.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CollectionService extends CrudService<Collection, Long, CollectionRepository> {

    @Autowired
    protected CollectionService(CollectionRepository repository) {
        super(repository, Collection.class);
    }

    @Override
    protected Page<Collection> findWithSearch(Pageable page, String search) {
        return null;
    }

}
