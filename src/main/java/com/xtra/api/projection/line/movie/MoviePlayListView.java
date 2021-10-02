package com.xtra.api.projection.line.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MoviePlayListView {
    private Long id;
    private String name;
    private String posterPath;
    private String backdropPath;
    private float rating;
    private String country;
    private LocalDate releaseDate;
}
