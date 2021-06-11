package com.xtra.api.projection.admin.video;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.Audio;
import com.xtra.api.model.EncodeStatus;
import com.xtra.api.model.Subtitle;
import com.xtra.api.projection.admin.videoInfo.VideoInfoView;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VideoView {
    private String location;
    private String token;
    private EncodeStatus encodeStatus;
    private List<Audio> audios;
    private List<Subtitle> subtitles;
    private VideoInfoView videoInfo;
}
