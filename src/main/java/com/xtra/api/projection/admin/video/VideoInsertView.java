package com.xtra.api.projection.admin.video;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.Audio;
import com.xtra.api.model.Subtitle;
import com.xtra.api.model.VideoInfo;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VideoInsertView {

    @NotBlank(message = "video location can not be empty")
    private String location;
    @NotNull(message = "audio list can not be empty")
    private List<Audio> audios;
    private List<Subtitle> subtitles;
    private List<VideoInfo> videoInfos;

}
