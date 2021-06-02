package com.xtra.api.projection.admin.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.Season;
import com.xtra.api.model.SeriesInfo;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeriesView {
    private Long id;
    private String name;
    private String year;
    private LocalDate lastUpdated;

    private SeriesInfo info;
    private Set<Long> collections;
    private List<Season> seasons;
}
