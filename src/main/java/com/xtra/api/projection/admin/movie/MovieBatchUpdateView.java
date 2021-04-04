package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieBatchUpdateView {
//    private String readNative;
//    private String streamAll;
//    private String directSource;
//    private String genTimestamps;
//    private String rtmpOutput;

    private Set<Long> movieIds;
    private Set<Long> serverIds;
    private Set<Long> collectionIds;

    private Boolean keepServers;
    private Boolean keepCollections;
}
