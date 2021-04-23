package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelBatchInsertView {

    private Set<Long> channelIds;
    private Set<Long> serverIds;
    private Set<Long> collectionIds;

    private Boolean keepServers;
    private Boolean keepCollections;
}
