package com.xtra.api.service;

import com.xtra.api.model.EncodeStatus;
import com.xtra.api.model.VideoInfo;
import com.xtra.api.model.Vod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;

public abstract class VodService<T extends Vod, R extends JpaRepository<T, Long>> extends CrudService<T, Long, R> {

    protected VodService(R repository, Class<T> aClass) {
        super(repository, aClass);
    }

    public void updateEncodeStatus(Long id, Map<String, String> encodeStatus) {
        T t = findByIdOrFail(id);
        t.setEncodeStatus(EncodeStatus.valueOf(encodeStatus.get("encodeStatus")));
        t.setLocation(encodeStatus.get("location"));
        repository.save(t);
    }

    public void updateMediaInfo(Long id, VideoInfo mediaInfo) {
        T t = findByIdOrFail(id);
        t.setMediaInfo(mediaInfo);
        repository.save(t);
    }
}
