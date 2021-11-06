package com.xtra.api.service.admin;


import com.xtra.api.mapper.admin.VideoMapper;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.vod.Video;
import com.xtra.api.projection.admin.video.EncodeRequest;
import com.xtra.api.projection.admin.video.EncodeResponse;
import com.xtra.api.projection.admin.video.VideoView;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Validated
public class VideoService extends CrudService<Video, Long, VideoRepository> {

    private final LoadBalancingService loadBalancingService;
    private final ServerService serverService;
    private final VideoMapper mapper;

    @Autowired
    protected VideoService(VideoRepository videoRepository, LoadBalancingService loadBalancingService, ServerService serverService, @Qualifier("videoMapperImpl") VideoMapper mapper) {
        super(videoRepository, "Video");
        this.loadBalancingService = loadBalancingService;
        this.serverService = serverService;
        this.mapper = mapper;
    }

    @Override
    protected Page<Video> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Video updateVideo(Long id, Video video) {
        return updateOrFail(id, video);
    }

    public VideoView getByToken(String vodToken) {
        return mapper.toVideoView(repository.findByToken(vodToken).orElse(null));
    }

    public String playVideo(String video_token, String line_token, HttpServletRequest request) {
        Video video = repository.findByToken(video_token).orElseThrow();
        ArrayList<Server> servers = loadBalancingService.findAvailableServersForVod(video);
        Server server = loadBalancingService.findLeastConnServerForVod(servers);

        return "http://" + server.getIp() + ":" + server.getCorePort() + "/vod/" + line_token + "/" + video_token;
    }

    public void updateEncodeStatus(Long id, String serverToken, EncodeResponse encodeResponse) {
        var video = findByIdOrFail(id);
        serverService.findByServerToken(serverToken).ifPresent(server -> {
            video.getVideoServers().stream().filter(videoServer -> videoServer.getId().getServerId()
                    .equals(server.getId())).findFirst().ifPresent(videoServer -> videoServer.setEncodeStatus(encodeResponse.getEncodeStatus()));
            video.getTargetVideosInfos().clear();
            video.getTargetVideosInfos().addAll(encodeResponse.getTargetVideoInfos().stream().map(mapper::toVideoInfo).collect(Collectors.toList()));
            repository.save(video);
        });
    }

    public void encode(Long id) {
        var video = findByIdOrFail(id);
        video.getVideoServers().forEach(videoServer -> {
            //@todo fix
            serverService.sendEncodeRequest(videoServer.getServer(), new EncodeRequest());
        });
    }

}
