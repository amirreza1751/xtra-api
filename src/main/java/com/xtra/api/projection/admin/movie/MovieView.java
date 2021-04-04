package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.MovieInfo;
import com.xtra.api.model.Video;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieView {
    private Long id;
    private String name;

    private MovieInfo info;
    private Set<Video> videos;

    private Set<Long> servers;
    private Set<Long> collections;
}
