package com.xtra.api.projection.admin.server.resource;

import lombok.Data;

@Data
public class ResourceView {

    private double cpuMaxFreq;
    private double cpuLoad;
    private double memoryTotal;
    private double memoryAvailable;
    private String networkName;
    private Long networkBytesSent;
    private Long networkBytesRecv;
    private int connections;
    private Long upTime;
}
