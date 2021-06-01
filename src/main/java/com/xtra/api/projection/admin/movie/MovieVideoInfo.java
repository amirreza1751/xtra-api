package com.xtra.api.projection.admin.movie;

import com.xtra.api.model.EncodeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

@Data
@AllArgsConstructor
public class MovieVideoInfo {
    private String location;
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private Duration duration;
    private EncodeStatus encodeStatus;
}
