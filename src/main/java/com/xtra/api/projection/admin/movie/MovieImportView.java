package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MovieImportView {
    private Set<MovieImport> movies;
    private Long sourceServer;
    private Set<Long> targetServers;
    private Set<Long> collections;
    private Set<Long> categories;
}
