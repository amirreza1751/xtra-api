package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.MovieInfo;
import com.xtra.api.projection.admin.video.AudioDetails;
import com.xtra.api.projection.admin.video.ServerIdEncodeStatus;
import com.xtra.api.projection.admin.video.SubtitleDetails;
import com.xtra.api.projection.admin.video.VideoInfoView;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieView {
    private Long id;
    private String name;

    private Long sourceServer;
    private String sourceLocation;
    private VideoInfoView sourceVideoInfo;

    private MovieInfo info;
    private List<VideoInfoView> targetVideosInfos;

    private List<SubtitleDetails> sourceSubtitles;
    private List<AudioDetails> sourceAudios;

    private Set<Long> collections;
    private Set<Long> categories;
    private Set<ServerIdEncodeStatus> servers;
}
