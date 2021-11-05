package com.xtra.api.projection.admin.connection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConnectionView {

    private Long id;
    private String streamName;
    private String serverName;
    private String lineUsername;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private long duration;
    private String userAgent;
    private String ip;
    private String isp;
    private String country;
    private String city;
    private String isoCode;
}
