package com.xtra.api.projection.admin.videoInfo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.Audio;
import com.xtra.api.model.EncodeStatus;
import com.xtra.api.model.Subtitle;
import lombok.Data;

import java.time.Duration;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VideoInfoView {
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private Duration duration;
}
