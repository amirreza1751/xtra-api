package com.xtra.api.projection.admin.collection;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CollectionInsertView {
    private Long id;
    private String name;
    private List<Long> channels = new ArrayList<>();
    private List<Long> movies = new ArrayList<>();
    private List<Long> series = new ArrayList<>();
    private List<Long> radios = new ArrayList<>();
}
