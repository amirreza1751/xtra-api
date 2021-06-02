package com.xtra.api.projection.admin.episode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.validator.constraints.URL;

import java.time.Duration;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class EpisodeVideoInfo {
    private String location;
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private Duration duration;
}
