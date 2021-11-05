package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChannelInfo {

    private Long id;
    private String name;
    private String logo;
    private boolean epg;
    private long totalUsers;
    private String link;
    private Set<ChannelServerInfo> channelInfos;
}
