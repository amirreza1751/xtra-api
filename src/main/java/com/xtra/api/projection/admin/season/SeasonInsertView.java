package com.xtra.api.projection.admin.season;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeasonInsertView {

    private String seasonName;
    private int seasonNumber;
    private int NoOfEpisodes;
    private LocalDate airDate;

}
