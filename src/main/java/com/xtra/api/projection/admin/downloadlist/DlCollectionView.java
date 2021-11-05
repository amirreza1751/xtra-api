package com.xtra.api.projection.admin.downloadlist;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DlCollectionView {
    private Long id;
    private String name;

    public DlCollectionView(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
