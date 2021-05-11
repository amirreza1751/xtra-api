package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelBatchInsertView {

    @NotNull(message = "Channel Ids can not be empty")
    private Set<Long> channelIds;
    @NotNull(message = "server Ids can not be empty")
    private Set<Long> serverIds;
    @NotNull(message = "collection Ids can not be empty")
    private Set<Long> collectionIds;

    private Boolean keepServers;
    private Boolean keepCollections;
}
