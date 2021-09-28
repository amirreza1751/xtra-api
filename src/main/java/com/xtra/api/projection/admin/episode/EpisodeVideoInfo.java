package com.xtra.api.projection.admin.episode;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

@Data
@AllArgsConstructor
public class EpisodeVideoInfo {
    private String location;
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private String link;
    private Duration duration;
}
