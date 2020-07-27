package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Entity;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Movie extends Vod {
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
