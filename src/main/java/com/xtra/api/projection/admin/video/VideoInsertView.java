package com.xtra.api.projection.admin.video;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class VideoInsertView {
    private String location;
    private List<AudioDetails> sourceAudios;
    private List<SubtitleDetails> sourceSubtitles;
}
