package com.xtra.api.mapper.admin;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.Episode;
import com.xtra.api.model.vod.Video;
import com.xtra.api.model.vod.VideoServer;
import com.xtra.api.model.vod.VideoServerId;
import com.xtra.api.projection.admin.episode.*;
import com.xtra.api.repository.ServerRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class EpisodeMapper {
    @Autowired
    private ServerRepository serverRepository;

    public abstract EpisodeView convertToView(Episode episode);

    public abstract EpisodeListView convertToListView(Episode episode);

    public abstract Episode convertToEntity(EpisodeInsertView episodeInsertView);

    @AfterMapping
    void assignServerIds(final EpisodeInsertView episodeInsertView, @MappingTarget final Episode episode) {
        if (episodeInsertView.getServers() != null) {
            for (Video video : episode.getVideos()) {
                Set<VideoServer> videoServers = new HashSet<>();
                for (Long id : episodeInsertView.getServers()) {
                    VideoServer videoServer = new VideoServer();
                    var server = serverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Server", id.toString()));
                    videoServer.setId(new VideoServerId(null, id));
                    videoServer.setServer(server);
                    videoServer.setVideo(video);
                    videoServers.add(videoServer);
                }
                video.setVideoServers(videoServers);
            }
        }
    }

    @AfterMapping
    void assignInfo(final Episode episode, @MappingTarget final EpisodeListView episodeListView){
        if (!episode.getVideos().isEmpty()){
            //set server info
            episodeListView.setServerInfoList(episode.getVideos().iterator().next().getVideoServers().stream().map(videoServer -> new EpisodeServerInfo(videoServer.getServer().getName())).collect(Collectors.toList()));
            //set video info
            episodeListView.setVideoInfos(episode.getVideos().stream().map(video -> {
                if (video.getVideoInfo() != null) {
                    return new EpisodeVideoInfo(video.getLocation(), video.getVideoInfo().getResolution(), video.getVideoInfo().getVideoCodec(), video.getVideoInfo().getAudioCodec(), video.getVideoInfo().getDuration());
                } else return null;
            }).collect(Collectors.toList()));
        }
        //set related season info and episode info
        if (episode.getSeason() != null){
            episodeListView.setSeasonNumber(episode.getSeason().getSeasonNumber());
            if (episode.getSeason().getSeries() != null){
                episodeListView.setSeriesName(episode.getSeason().getSeries().getName());
            }
        }
    }

    @AfterMapping
    public void convertToServerIds(final Episode episode, @MappingTarget final EpisodeView episodeView){
        Set<Long> servers = new HashSet<>();
        for (Video video : episode.getVideos()){
            servers.addAll(video.getVideoServers().stream().map(videoServer -> videoServer.getServer().getId()).collect(Collectors.toSet()));
        }
        episodeView.setServers(servers);
    }

    public Set<VideoServer> convertToVideoServers(Set<Long> serverIds, Episode episode){
        Set<VideoServer> videoServers = new HashSet<>();
        if (serverIds.size() > 0 ){
            for (Video video : episode.getVideos()){
                for (Long serverId : serverIds){
                    var server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server", serverId.toString()));
                    VideoServer videoServer = new VideoServer(new VideoServerId(video.getId(), serverId));
                    videoServer.setVideo(video);
                    videoServer.setServer(server);
                    videoServers.add(videoServer);
                }
            }
        }
        return videoServers;
    }
}
