package com.xtra.api.projection.admin.video;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.Audio;
import com.xtra.api.model.vod.Subtitle;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VideoInsertView {
    private Long id;
    private String location;
    private List<Audio> audios;
    private List<Subtitle> subtitles;
}
