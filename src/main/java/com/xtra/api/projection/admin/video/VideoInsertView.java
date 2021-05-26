package com.xtra.api.projection.admin.video;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.Audio;
import com.xtra.api.model.Subtitle;
import com.xtra.api.model.VideoInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VideoInsertView {
    private String location;
    private List<Audio> audios;
    private List<Subtitle> subtitles;
    private VideoInfo videoInfo;

}
