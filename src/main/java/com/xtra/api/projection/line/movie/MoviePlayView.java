package com.xtra.api.projection.line.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MoviePlayView {
    private Long id;
    private String link;

    private String name;
    private int duration;
    private String posterPath;
    private String backdropPath;
    private String plot;
    private String cast;
    private String director;
    private String genres;
    private LocalDate releaseDate;
    private int runtime;
    private String youtubeTrailer;
    private float rating;
    private String country;
}
