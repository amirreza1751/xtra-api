package com.xtra.api.projection.admin.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.SeriesInfo;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeriesInsertView {
    private String name;
    private String year;
    private LocalDate lastUpdated;

    private SeriesInfo info;
    private Set<Long> collections;
}
