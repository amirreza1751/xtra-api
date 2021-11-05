package com.xtra.api.projection.admin.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.Season;
import com.xtra.api.model.vod.SeriesInfo;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeriesView {
    private Long id;
    private String name;
    private String year;
    private LocalDate lastUpdated;

    private SeriesInfo info;
    private Set<Long> collections;
    private Set<Long> categories;
    private List<Season> seasons;
}
