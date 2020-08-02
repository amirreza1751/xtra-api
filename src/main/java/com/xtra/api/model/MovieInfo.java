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
    private String poster_path;
    @URL
    private String backdrop_path;
    private String plot;
    private String cast;
    private String director;
    private String genres;
    private LocalDate release_date;
    private int runtime;
    private String youtube_trailer;
    private float rating;
    private String country;
}
