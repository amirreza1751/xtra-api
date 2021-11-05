package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.Resolution;
import com.xtra.api.projection.admin.video.VideoInsertView;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MovieImport {
    private String name;
    private int tmdbId;
    private VideoInsertView sourceVideo;
    private List<Resolution> targetResolutions;
}
