package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.CollectionType;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CollectionInsertDto {
    private Long id;
    private String name;
    private CollectionType type;
    private List<Long> channels;
    private List<Long> movies;
    private List<Long> series;
    private List<Long> radios;
}
