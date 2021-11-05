package com.xtra.api.projection.admin.downloadlist;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DownloadListView {
    private Long id;
    private String name;

    private List<DlCollectionView> collections;
}
