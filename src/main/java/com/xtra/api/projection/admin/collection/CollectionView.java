package com.xtra.api.projection.admin.collection;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.MediaPair;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CollectionView {
    private Long id;
    private String name;
    private String categoryName;
    private Set<MediaPair<Long, String>> channels;
    private Set<MediaPair<Long, String>> movies;
    private Set<MediaPair<Long, String>> series;
    private Set<MediaPair<Long, String>> radios;
}
