package com.xtra.api.projection.admin.movie;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.vod.Resolution;
import com.xtra.api.projection.admin.video.AudioDetails;
import com.xtra.api.projection.admin.video.SubtitleDetails;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MovieInsertView {
    private String name;
    private MovieInfoInsertView info;

    private Long sourceServer;
    private String sourceLocation;
    private List<AudioDetails> sourceAudios;
    private List<SubtitleDetails> sourceSubtitles;

    private List<Resolution> targetResolutions;
    private Set<Long> targetServers;
    private Set<Long> collections;
    private Set<Long> categories;
}
