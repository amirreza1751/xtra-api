package com.xtra.api.model;

import lombok.Data;
import lombok.Generated;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
@Data
public class MovieInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @URL
    private String posterURL;
    @URL
    private String backdropURL;
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
