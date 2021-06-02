package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.MovieInfo;
import com.xtra.api.model.Video;
import com.xtra.api.model.VideoInfo;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieListView {
    private Long id;
    private String name;
    private int duration;
    private List<MovieServerInfo> serverInfoList;
    private List<MovieVideoInfo> videoInfoList;
}
