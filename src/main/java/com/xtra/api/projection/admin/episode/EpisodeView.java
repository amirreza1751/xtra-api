package com.xtra.api.projection.admin.episode;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.Set;

@Data
public class EpisodeView {
    private Long id;
    private int episodeNumber;
    private String episodeName;
    private String notes;
    @URL
    private String imageUrl;
    private String plot;
    private LocalDate releaseDate;
    private int runtime;
    private float rating;

//    private Set<Video> videos;
}
