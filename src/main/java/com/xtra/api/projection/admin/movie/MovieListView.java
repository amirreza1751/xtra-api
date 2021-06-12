package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieListView {
    private Long id;
    private String name;
    private int duration;
    private List<MovieServerInfo> serverInfoList;
    private List<MovieVideoInfo> videoInfoList;
}
