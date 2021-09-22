package com.xtra.api.projection.admin.category;

import com.xtra.api.projection.EntityListItem;
import lombok.Data;

import java.util.List;

@Data
public class CategorySummaryView {
    private List<EntityListItem> channelCategories;
    private List<EntityListItem> radioCategories;
    private List<EntityListItem> movieCategories;
    private List<EntityListItem> SeriesCategories;
}
