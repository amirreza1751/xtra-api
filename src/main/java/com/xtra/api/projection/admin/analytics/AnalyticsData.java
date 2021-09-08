package com.xtra.api.projection.admin.analytics;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsData {
    private long resellerCount;
    private long pendingResellerCount;
    private long onlineUsersCount;
    private long connectionsCount;
    private long onlineChannelsCount;
    private long offlineChannelsCount;
    private long totalInput;
    private long totalOutput;

    List<ServerSummary> serverSummaryList;
}
