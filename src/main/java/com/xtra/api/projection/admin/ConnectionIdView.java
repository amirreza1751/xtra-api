package com.xtra.api.projection.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConnectionIdView {
    private Long lineId;
    private Long streamId;
    private Long serverId;
    private String userIp;
}
