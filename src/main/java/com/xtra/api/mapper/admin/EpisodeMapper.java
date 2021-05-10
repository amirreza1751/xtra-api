package com.xtra.api.mapper.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.episode.EpisodeListView;
import com.xtra.api.projection.admin.episode.EpisodeView;
import com.xtra.api.repository.ServerRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

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


}
