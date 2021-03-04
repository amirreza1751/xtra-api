package com.xtra.api.projection.admin.category;

import lombok.Data;

@Data
public class CategoryView {
    private String name;
    private int channelCount;
    private int radioCount;
    private int movieCount;
    private int seriesCount;
}
