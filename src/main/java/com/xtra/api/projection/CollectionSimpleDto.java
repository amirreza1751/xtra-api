package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.CollectionType;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CollectionSimpleDto {
    private Long id;
    private String name;
    private CollectionType type;
    private int channels;
    private int movies;
    private int series;
    private int radios;
}
