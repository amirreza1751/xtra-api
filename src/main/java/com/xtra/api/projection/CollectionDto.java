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
    private List<Channel> channels;
    private List<Movie> movies;
    private List<Series> series;
    private List<Radio> radios;
}
