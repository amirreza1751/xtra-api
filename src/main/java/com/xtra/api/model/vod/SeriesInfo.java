package com.xtra.api.model.vod;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeriesInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @URL
    private String posterUrl;
    @URL
    private String backdropUrl;

    @Column(columnDefinition = "TEXT")
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
