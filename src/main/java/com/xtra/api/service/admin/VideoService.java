package com.xtra.api.service.admin;


import com.xtra.api.model.vod.Video;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class VideoService extends CrudService<Video, Long, VideoRepository> {


    @Autowired
    protected VideoService(VideoRepository videoRepository){
        super(videoRepository, "Video");
    }

    @Override
    protected Page<Video> findWithSearch(String search, Pageable page) {
        return null;
    }

    @Override
    public Video updateOrFail(Long id, Video newVideo){
        Video oldVideo = findByIdOrFail(id);
        copyProperties(newVideo, oldVideo, "id", "token", "subtitles", "audios", "videoServers");
        oldVideo.getSubtitles().retainAll(newVideo.getSubtitles());
        oldVideo.getSubtitles().addAll(newVideo.getSubtitles());
        oldVideo.getAudios().retainAll(newVideo.getAudios());
        oldVideo.getAudios().addAll(newVideo.getAudios());
        return repository.save(oldVideo);
    }

    public Video updateVideo(Long id, Video video) {
        return updateOrFail(id, video);
    }

    public Video getByToken(String vodToken) {
        return repository.findByToken(vodToken).orElse(null);
    }
}
