package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DlCollectionDto {
    private Long id;
    private String name;

    public DlCollectionDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
