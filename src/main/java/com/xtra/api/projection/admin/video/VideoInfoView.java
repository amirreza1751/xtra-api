package com.xtra.api.projection.admin.video;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.AudioCodec;
import com.xtra.api.model.vod.Resolution;
import com.xtra.api.model.vod.VideoCodec;
import lombok.Data;

import java.time.Duration;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoInfoView {
    private Duration duration;
    private Resolution resolution;
    private VideoCodec videoCodec;
    private AudioCodec audioCodec;
    private String fileSize;
}
