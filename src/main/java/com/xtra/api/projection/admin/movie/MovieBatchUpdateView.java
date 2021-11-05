package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.Resolution;
import lombok.Data;

import java.util.List;
import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MovieBatchUpdateView {
    private Set<Long> movies;
    private Set<Long> targetServers;
    private Set<Long> collections;
    private List<Resolution> targetResolutions;

    private boolean addResolutions;
    private boolean addServers;
    private boolean addCollections;
}
