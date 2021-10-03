package com.xtra.api.mapper.line;

import com.xtra.api.model.vod.Episode;
import com.xtra.api.model.vod.Season;
import com.xtra.api.model.vod.Series;
import com.xtra.api.projection.line.series.SeasonPlayView;
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
    @Mapping(source = "info.genres", target = "genres")
    @Mapping(source = "info.releaseDate", target = "releaseDate")
    @Mapping(source = "info.runtime", target = "runtime")
    @Mapping(source = "info.rating", target = "rating")
    @Mapping(source = "info.country", target = "country")
    @Mapping(target = "categories", ignore = true)
    public abstract SeriesPlayListView convertToPlaylist(Series series);

    @AfterMapping
    public void assignCollections(final Series series, @MappingTarget SeriesPlayListView playListView) {
        playListView.setCategories(emptyIfNull(series.getCategories())
                .stream().map(categoryVod -> categoryVod.getCategory().getName()).collect(Collectors.toSet()));
    }

    @Mapping(source = "info.posterUrl", target = "posterPath")
    @Mapping(source = "info.posterUrl", target = "backdropPath")
    @Mapping(source = "info.genres", target = "genres")
    @Mapping(source = "info.releaseDate", target = "releaseDate")
    @Mapping(source = "info.runtime", target = "runtime")
    @Mapping(source = "info.rating", target = "rating")
    @Mapping(source = "info.country", target = "country")
    @Mapping(target = "categories", ignore = true)
    public abstract SeriesPlayView convertToPlayView(Series series);

    @AfterMapping
    public void assignCollectionsAndLink(final Series series, @MappingTarget SeriesPlayView playView) {
        playView.setCategories(emptyIfNull(series.getCategories())
                .stream().map(categoryVod -> categoryVod.getCategory().getName()).collect(Collectors.toSet()));

        playView.getSeasons().iterator().next().getEpisodes().iterator().next().setLinks(series.getSeasons().iterator().next().getEpisodes().iterator().next().getVideos().stream().map(video -> {
            var line = getCurrentLine();
            return "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + video.getToken();
        }).collect(Collectors.toList()));

//        for (Season season: series.getSeasons()) {
//            for (Episode episode: season.getEpisodes()) {
//                if (!episode.getVideos().isEmpty()) {
//                    //set video info
//                    playView.setLinks(series.getSeasons().stream().map(video -> {
//                        if (video.getVideoInfo() != null) {
//                            var line = getCurrentLine();
//                            return "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + video.getToken();
//                        } else return "";
//                    }).collect(Collectors.toList()));
//                }
//            }
//        }

    }
}
