package com.xtra.api.projection.admin.episode;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpisodeImportView {
    private Set<EpisodeImport> episodes;
    private Long sourceServer;
    private Set<Long> targetServers;
    private String notes;
}
