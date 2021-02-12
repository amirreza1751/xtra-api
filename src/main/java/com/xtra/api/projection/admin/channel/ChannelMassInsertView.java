package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelMassInsertView {
    private String readNative;
    private String streamAll;
    private String directSource;
    private String genTimestamps;
    private String rtmpOutput;

    private Set<Long> channelIds;
}
