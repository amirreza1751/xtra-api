package com.xtra.api.service;

import com.xtra.api.model.EncodeStatus;
import com.xtra.api.model.Vod;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class VodService<T extends Vod, Long, R extends JpaRepository<T, Long>> extends CrudService<T, Long, R> {

    protected VodService(R repository, Class<T> aClass) {
        super(repository, aClass);
    }

    public void setEncodeStatus(Long id, EncodeStatus encodeStatus) {
        T t = findByIdOrFail(id);
        t.setEncodeStatus(encodeStatus);
        repository.save(t);
    }
}
