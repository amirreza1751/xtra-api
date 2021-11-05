package com.xtra.api.projection.line.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeriesPlayListView {
    private Long id;
    private String name;
    private String year;
    private LocalDate lastUpdated;
    private String posterPath;
    private String backdropPath;
    private String genres;
    private LocalDate releaseDate;
    private int runtime;
    private float rating;
    private String country;

    private Set<String> categories;
}
