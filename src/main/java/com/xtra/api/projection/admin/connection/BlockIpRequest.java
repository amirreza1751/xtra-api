package com.xtra.api.projection.admin.connection;

import lombok.Data;

import java.time.Duration;

@Data
public class BlockIpRequest {
    //todo validation
    private String ipAddress;
    private Duration duration;
}
