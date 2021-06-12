package com.xtra.api.projection.admin.videoInfo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.Duration;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VideoInfoView {
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private Duration duration;
}
