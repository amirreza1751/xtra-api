package com.xtra.api.projection.line.series;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SeasonPlayView {
    private Long id;

    private String seasonName;
    private int seasonNumber;
    private int NoOfEpisodes;
    private LocalDate airDate;
    private List<EpisodePlayView> episodes;

}
