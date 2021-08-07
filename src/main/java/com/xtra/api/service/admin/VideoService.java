package com.xtra.api.service.admin;


import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Channel;
import com.xtra.api.model.vod.Video;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class VideoService extends CrudService<Video, Long, VideoRepository> {

    private final LoadBalancingService loadBalancingService;

    @Autowired
    protected VideoService(VideoRepository videoRepository, LoadBalancingService loadBalancingService){
        super(videoRepository, "Video");
        this.loadBalancingService = loadBalancingService;
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

    public String playVideo(String video_token, String line_token, HttpServletRequest request) {
        Video video = repository.findByToken(video_token).orElseThrow();
        ArrayList<Server> servers = loadBalancingService.findAvailableServersForVod(video);
        Server server = loadBalancingService.findLeastConnServerForVod(servers);

        return "http://" + server.getIp() + ":" + server.getCorePort() + "/vod/" + line_token + "/" + video_token;
    }
}
