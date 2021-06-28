package com.xtra.api.projection.admin.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.model.stream.StreamProtocol;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class ActivityLogView {
    private String lineUsername;
    private String streamName;
    private String serverName;
    private Long lineId;
    private Long streamId;
    private Long serverId;
    private String ip;
    private String player;
    private String country;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime stop;
    private String duration;
    private StreamProtocol output;
}
