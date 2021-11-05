package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.EncodeStatus;
import com.xtra.api.projection.admin.video.ServerEncodeStatus;
import com.xtra.api.projection.admin.video.VideoInfoView;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MovieListView {
    private Long id;
    private String name;
    private int duration;
    private String link;
    private EncodeStatus encodeStatus;
    private List<ServerEncodeStatus> servers;
    private List<VideoInfoView> targetVideos;
}
