package com.xtra.api.projection.admin.collection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.MediaType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CollectionInsertView {
    private Long id;
    @NotBlank(message = "collection name can not be empty")
    private String name;
    @NotBlank(message = "collection category can not be empty")
    private String categoryName;
    private MediaType type;
    private List<Long> channels;
    private List<Long> movies;
    private List<Long> series;
    private List<Long> radios;
}
