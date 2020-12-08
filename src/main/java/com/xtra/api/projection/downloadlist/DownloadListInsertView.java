package com.xtra.api.projection.downloadlist;

import lombok.Data;

import java.util.Set;

@Data
public class DownloadListInsertView {
    private Long id;
    private String name;

    private Set<Long> collections;
}
