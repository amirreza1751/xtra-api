package com.xtra.api.projection.admin.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeriesBatchUpdateView {

    private Set<Long> SeriesIds;
    private Set<Long> collectionIds;

    private boolean keepCollections;
}
