package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.MovieInfo;
import com.xtra.api.model.Video;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieInsertView {
    private Long id;
    @NotBlank( message = "movie name can not be empty" )
    private String name;

    private MovieInfo info;
    @NotNull(message = "video list can not be empty")
    private Set<Video> videos;

    @NotNull(message = "server list can not be empty")
    private Set<Long> servers;
    private Set<Long> collections;
}
