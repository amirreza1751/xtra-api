package com.xtra.api.service;

import com.xtra.api.model.Radio;
import com.xtra.api.repository.RadioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RadioService extends CrudService<Radio, Long, RadioRepository> {

    @Autowired
    protected RadioService(RadioRepository repository) {
        super(repository, Radio.class);
    }

    @Override
    protected Page<Radio> findWithSearch(Pageable page, String search) {
        return null;
    }
}
