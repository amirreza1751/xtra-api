package com.xtra.api.projection.admin.analytics;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsData {
    private long resellerCount;
    private long pendingResellerCount;
    private long onlineUserCount;
    private long connectionsCount;
    private long onlineChannelsCount;
    private long offlineChannelsCount;
    private int totalInput;
    private int totalOutput;

    private List<ServerSummary> serverSummaryList;
}
