package com.xtra.api.projection.collection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.MediaType;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CollectionSimplifiedView {
    private Long id;
    private String name;
    private MediaType type;
    private int channels;
    private int movies;
    private int series;
    private int radios;
}
