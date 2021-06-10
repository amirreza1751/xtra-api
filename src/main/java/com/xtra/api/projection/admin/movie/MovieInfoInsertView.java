package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.MovieInfo;
import com.xtra.api.projection.admin.video.VideoView;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieInfoInsertView {
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
