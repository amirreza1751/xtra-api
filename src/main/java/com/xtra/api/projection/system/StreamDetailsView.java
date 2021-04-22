package com.xtra.api.projection.system;

import com.xtra.api.model.StreamStatus;
import lombok.Data;

@Data
public class StreamDetailsView {
    private Long streamId;
    private String uptime;
    private String currentInput;
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private String speed;
    private String frameRate;
    private String bitrate;
    private StreamStatus streamStatus;
}
