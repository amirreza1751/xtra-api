package com.xtra.api.projection.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ConnectionView {

    private ConnectionIdView connectionIdView;
    private String streamName;
    private String serverName;
    private String lineUsername;
//    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private long duration;
    private String userAgent;
    private String isp;
    private String country;
    private String city;
    private String isoCode;
}
