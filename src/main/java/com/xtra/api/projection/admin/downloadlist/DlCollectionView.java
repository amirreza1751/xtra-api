package com.xtra.api.projection.admin.downloadlist;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DlCollectionView {
    private Long id;
    private String name;

    public DlCollectionView(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
