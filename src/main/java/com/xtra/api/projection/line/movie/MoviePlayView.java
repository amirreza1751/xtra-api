package com.xtra.api.projection.line.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.movie.MovieServerInfo;
import com.xtra.api.projection.admin.movie.MovieVideoInfo;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MoviePlayView {
    private Long id;
    private List<String> links;

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
