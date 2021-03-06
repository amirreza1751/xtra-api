package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.video.VideoInsertView;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieInsertView {
    private String name;

    private MovieInfoInsertView info;
    private Set<VideoInsertView> videos;

    private Set<Long> servers;
    private Set<Long> collections;
}
