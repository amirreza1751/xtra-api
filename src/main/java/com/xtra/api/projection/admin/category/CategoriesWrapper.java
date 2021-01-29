package com.xtra.api.projection.admin.category;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CategoriesWrapper {
    private List<String> channelCategories;
    private List<String> radioCategories;
    private List<String> movieCategories;
    private List<String> seriesCategories;
}
