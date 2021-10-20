package com.xtra.api.mapper.line;

import com.xtra.api.model.vod.Episode;
import com.xtra.api.model.vod.Series;
import com.xtra.api.projection.line.series.EpisodePlayView;
import com.xtra.api.projection.line.series.SeriesPlayListView;
import com.xtra.api.projection.line.series.SeriesPlayView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Value;

import java.util.stream.Collectors;

import static com.xtra.api.service.system.UserAuthService.getCurrentLine;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Mapper(componentModel = "spring")
public abstract class LineSeriesMapper {
    @Value("${server.external.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    @Mapping(source = "info.posterUrl", target = "posterPath")
    @Mapping(source = "info.posterUrl", target = "backdropPath")
    @Mapping(source = "info", target = ".")
    @Mapping(target = "categories", ignore = true)
    public abstract SeriesPlayListView convertToPlaylist(Series series);

    @AfterMapping
    public void assignCollections(final Series series, @MappingTarget SeriesPlayListView playListView) {
        playListView.setCategories(emptyIfNull(series.getCategories())
                .stream().map(categoryVod -> categoryVod.getCategory().getName()).collect(Collectors.toSet()));

        playListView.setId(series.getId());
    }

    @Mapping(source = "info.posterUrl", target = "posterPath")
    @Mapping(source = "info.posterUrl", target = "backdropPath")
    @Mapping(target = "categories", ignore = true)
    public abstract SeriesPlayView convertToPlayView(Series series);

    @AfterMapping
    public void assignCollectionsAndLink(final Series series, @MappingTarget SeriesPlayView playView) {
        playView.setCategories(emptyIfNull(series.getCategories())
                .stream().map(categoryVod -> categoryVod.getCategory().getName()).collect(Collectors.toSet()));

    }

    public abstract EpisodePlayView convertToEpisodePlayView(Episode episode);

    @AfterMapping
    public void addLinksToEpisodes(final Episode episode, @MappingTarget final EpisodePlayView playView) {
        var line = getCurrentLine();
        playView.setLinks(episode.getVideos().stream().map(video -> "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + video.getToken()).collect(Collectors.toList()));
    }
}
