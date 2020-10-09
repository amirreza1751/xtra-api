package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.*;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CollectionDto {
    private Long id;
    private String name;
    private CollectionType type;
    private List<MediaPair<Long, String>> channels;
    private List<MediaPair<Long, String>> movies;
    private List<MediaPair<Long, String>> series;
    private List<MediaPair<Long, String>> radios;
}
