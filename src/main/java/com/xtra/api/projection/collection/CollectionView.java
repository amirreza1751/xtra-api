package com.xtra.api.projection.collection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CollectionView {
    private Long id;
    private String name;
    private CollectionType type;
    private Set<MediaPair<Long, String>> channels;
    private Set<MediaPair<Long, String>> movies;
    private Set<MediaPair<Long, String>> series;
    private Set<MediaPair<Long, String>> radios;
}
