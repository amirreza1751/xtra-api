package com.xtra.api.projection.admin.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.Season;
import com.xtra.api.model.SeriesInfo;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeriesListView {
    private Long id;
    private String name;
    private String[] collections;
    private int seasons;
    private int episodes;
    private LocalDate releaseDate;
}
