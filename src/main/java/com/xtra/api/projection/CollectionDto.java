package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.Channel;
import com.xtra.api.model.Movie;
import com.xtra.api.model.Radio;
import com.xtra.api.model.Series;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CollectionDto {
    private String name;
    private List<Channel> channels;
    private List<Movie> movies;
    private List<Series> series;
    private List<Radio> radios;
}
