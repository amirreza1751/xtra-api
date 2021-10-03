package com.xtra.api.projection.line.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.Season;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeriesPlayView {
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
    private List<SeasonPlayView> seasons;
}
