package com.xtra.api.service.admin;

import com.xtra.api.model.stream.Radio;
import com.xtra.api.repository.RadioRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RadioService extends CrudService<Radio, Long, RadioRepository> {

    @Autowired
    protected RadioService(RadioRepository repository) {
        super(repository, "Radio");
    }

    @Override
    protected Page<Radio> findWithSearch(String search, Pageable page) {
        return null;
    }
}
