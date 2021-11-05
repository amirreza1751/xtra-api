package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChannelBatchInsertView {

    private Set<Long> channelIds;
    private Set<Long> serverIds;
    private Set<Long> collectionIds;

    // null => no change | TRUE OR FALSE TO CHANGE
    private String generatePts;
    private String nativeFrames;
    private String streamAllCodecs;
    private String allowRecording;
    private String outputRTMP;
    private String directSource;

    // null => no change | EMPTY STRING TO CLEAR, FILL TO SAVE
    private String customChannelSID;
    private String onDemandProbeSize;
    private String minuteDelay;
    private String userAgent;
    private String httpProxy;
    private String cookie;
    private String headers;
    private String transcodingProfile;

    private Boolean keepServers;
    private Boolean keepCollections;
}
