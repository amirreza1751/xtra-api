package com.xtra.api.projection.line.series;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.List;

@Data
public class EpisodePlayView {
    private Long id;
    private String episodeName;
    private String imageUrl;
    private String plot;
    private LocalDate releaseDate;
    private int runtime;
    private float rating;
    private List<String> links;
}