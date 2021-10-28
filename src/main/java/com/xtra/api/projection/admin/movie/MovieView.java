package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.MovieInfo;
import com.xtra.api.projection.admin.video.VideoView;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieView {
    private Long id;
    private String name;

    private MovieInfo info;
    private Set<VideoView> videos;

    private Set<Long> collections;
    private Set<Long> categories;
    private Set<Long> servers;
}
