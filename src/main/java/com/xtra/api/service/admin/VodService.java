package com.xtra.api.service.admin;

import com.xtra.api.model.vod.Vod;
import com.xtra.api.service.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class VodService<T extends Vod, R extends JpaRepository<T, Long>> extends CrudService<T, Long, R> {

    protected VodService(R repository) {
        super(repository, "VOD");
    }

}
