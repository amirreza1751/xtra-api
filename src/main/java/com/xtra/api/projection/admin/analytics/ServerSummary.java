package com.xtra.api.projection.admin.analytics;


public interface ServerSummary {
    String getName();

    String getDomainName();

    String getIp();

    String getCorePort();

    String getNginxPort();

    String getInterfaceName();

    Long getConnectionsCount();

    Long getOnlineUsersCount();

    Long getOnlineChannelsCount();

    Long getOfflineChannelsCount();

    Double getCpuMaxFreq();

    Double getCpuCurrentFreq();

    Double getMemoryTotal();

    Double getMemoryAvailable();

    String getNetworkName();

    Long getNetworkBytesSent();

    Long getNetworkBytesRecv();

    Long getUpTime();
}
