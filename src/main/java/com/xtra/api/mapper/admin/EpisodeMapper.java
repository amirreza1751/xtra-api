package com.xtra.api.mapper.admin;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.vod.Episode;
import com.xtra.api.model.vod.VideoInfo;
import com.xtra.api.model.vod.VideoServer;
import com.xtra.api.model.vod.VideoServerId;
import com.xtra.api.projection.admin.episode.EpisodeInsertView;
import com.xtra.api.projection.admin.episode.EpisodeListView;
import com.xtra.api.projection.admin.episode.EpisodeView;
import com.xtra.api.projection.admin.video.VideoInfoView;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.ServerRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class EpisodeMapper {
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private LineRepository lineRepository;

    @Value("${server.external.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    public abstract EpisodeView convertToView(Episode episode);

    public abstract EpisodeListView convertToListView(Episode episode);

    public abstract Episode updateEntity(EpisodeInsertView episodeInsertView, @MappingTarget final Episode episode);

    public abstract Episode convertToEntity(EpisodeInsertView episodeInsertView);

    @AfterMapping
    void assignServerRelations(final EpisodeInsertView episodeInsertView, @MappingTarget final Episode episode) {
        if (episodeInsertView.getTargetServers() != null) {
            var video = episode.getVideo();
            Set<VideoServer> videoServers = new HashSet<>();
            for (Long id : episodeInsertView.getTargetServers()) {
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

    @AfterMapping
    void assignInfo(final Episode episode, @MappingTarget final EpisodeListView episodeListView) {
        if (episode.getVideo() != null) {
            episodeListView.setServers(episode.getVideo().getVideoServers().stream().map(videoServer -> videoServer.getServer().getName()).collect(Collectors.toList()));
            episodeListView.setTargetVideos(episode.getVideo().getTargetVideosInfos().stream().map(this::toVideoInfoView).collect(Collectors.toList()));

            var video = episode.getVideo();
            var system_line = lineRepository.findByUsername("system_line");
            String link = system_line.map(line -> "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + video.getToken()).orElse("");
            episodeListView.setLink(link);
        }

        //set related season info and episode info
        if (episode.getSeason() != null) {
            episodeListView.setSeasonNumber(episode.getSeason().getSeasonNumber());
            if (episode.getSeason().getSeries() != null) {
                episodeListView.setSeriesName(episode.getSeason().getSeries().getName());
            }
        }
    }

    @AfterMapping
    public void convertToServerIds(final Episode episode, @MappingTarget final EpisodeView episodeView) {
        var video = episode.getVideo();
        Set<Long> servers = video.getVideoServers().stream().map(videoServer -> videoServer.getServer().getId()).collect(Collectors.toSet());
        episodeView.setServers(servers);
        if (episode.getSeason() != null) {
            if (episode.getSeason().getSeries() != null) {
                episodeView.setSeriesTmdbId(episode.getSeason().getSeries().getInfo().getTmdbId());
            }
        }
    }

    abstract VideoInfoView toVideoInfoView(VideoInfo videoInfo);

}
