package com.xtra.api.projection.admin.episode;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EpisodeBatchUpdateView {

    private Set<Long> episodeIds;
    private Set<Long> serverIds;

    private boolean keepServers;
}
