package com.xtra.api.projection.line.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoviePlayListView {
    private Long id;
    private String name;
    private String posterPath;
    private String backdropPath;
    private float rating;
    private String country;
    private LocalDate releaseDate;

    private Set<Long> categories;
}
