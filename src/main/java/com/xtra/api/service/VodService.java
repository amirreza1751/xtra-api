package com.xtra.api.service;

import com.xtra.api.model.EncodeStatus;
import com.xtra.api.model.Vod;
import com.xtra.api.repository.VodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VodService extends CrudService<Vod,Long, VodRepository> {

    @Autowired
    protected VodService(VodRepository vodRepository) {
        super(vodRepository, Vod.class);
    }

    @Override
    protected Page<Vod> findWithSearch(Pageable page, String search) {
        return null;
    }

    public void setEncodeStatus(Long id, EncodeStatus encodeStatus) {
        Vod vod = findByIdOrFail(id);
        vod.setEncodeStatus(encodeStatus);
        repository.save(vod);
    }
}
