package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.MovieInfo;
import com.xtra.api.model.StreamType;
import com.xtra.api.model.Video;
import com.xtra.api.projection.admin.epg.EpgDetails;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieInsertView {
    private Long id;
    private String name;

    private MovieInfo info;
    private Set<Video> videos;

    private Set<Long> servers;
    private Set<Long> collections;
}
