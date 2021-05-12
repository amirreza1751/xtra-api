package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MovieBatchUpdateView {
//    private String readNative;
//    private String streamAll;
//    private String directSource;
//    private String genTimestamps;
//    private String rtmpOutput;

    @NotNull(message = "movie list can not be empty")
    private Set<Long> movieIds;
    @NotNull(message = "server list can not be empty")
    private Set<Long> serverIds;
    @NotNull(message = "collection list can not be empty")
    private Set<Long> collectionIds;

    private Boolean keepServers;
    private Boolean keepCollections;
}
