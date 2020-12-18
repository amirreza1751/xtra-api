package com.xtra.api.service;


import com.xtra.api.model.Server;
import com.xtra.api.model.Video;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VideoService extends CrudService<Video, Long, VideoRepository> {


    @Autowired
    protected VideoService(VideoRepository videoRepository){
        super(videoRepository, Video.class);
    }

    @Override
    protected Page<Video> findWithSearch(Pageable page, String search) {
        return null;
    }


    public Video updateVideo(Long id, Video video) {
        return updateOrFail(id, video);
    }
}
