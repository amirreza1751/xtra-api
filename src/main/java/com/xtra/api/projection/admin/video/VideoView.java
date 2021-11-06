package com.xtra.api.projection.admin.video;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.Audio;
import com.xtra.api.model.vod.EncodeStatus;
import com.xtra.api.model.vod.Resolution;
import com.xtra.api.model.vod.Subtitle;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoView {
    private Long id;
    private String sourceLocation;
    private String token;
    private EncodeStatus encodeStatus;
    private List<Audio> sourceAudios;
    private List<Subtitle> sourceSubtitles;
    private VideoInfoView sourceVideoInfo;
    private List<Resolution> targetResolutions;

}
