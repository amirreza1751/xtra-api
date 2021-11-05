package com.xtra.api.model.vod;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.time.Duration;

@Data
@Entity
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VideoInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Duration duration;
    private Resolution resolution;

    @Enumerated(EnumType.STRING)
    private VideoCodec videoCodec;

    @Enumerated(EnumType.STRING)
    private AudioCodec audioCodec;

    private String fileSize;
}
