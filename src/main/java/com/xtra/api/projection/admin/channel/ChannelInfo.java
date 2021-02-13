package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamType;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelInfo {

    private Long id;
    private String name;
    private String logo;
    private Set<MergedChannelInfo> channelInfos;
}
