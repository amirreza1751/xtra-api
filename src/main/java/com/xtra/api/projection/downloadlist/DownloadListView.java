package com.xtra.api.projection.downloadlist;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DownloadListView {
    private Long id;
    private String name;

    private List<DlCollectionView> collections;
}
