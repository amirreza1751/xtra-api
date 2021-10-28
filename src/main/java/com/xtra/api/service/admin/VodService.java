package com.xtra.api.service.admin;

import com.xtra.api.model.vod.Video;
import com.xtra.api.model.vod.Vod;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.xtra.api.util.Utilities.generateRandomString;

public abstract class VodService<T extends Vod, R extends JpaRepository<T, Long>> extends CrudService<T, Long, R> {

    protected final VideoRepository videoRepository;

    protected VodService(R repository, VideoRepository videoRepository) {
        super(repository, "VOD");
        this.videoRepository = videoRepository;
    }

    public void generateVideoToken(Video video) {
        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (videoRepository.findByToken(token).isPresent());
        video.setToken(token);
    }

}
