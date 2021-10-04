package com.xtra.api.projection.admin.category;

import com.xtra.api.model.MediaType;
import com.xtra.api.projection.EntityListItem;
import lombok.Data;

import java.util.List;

@Data
public class CategoryView {
    private Long id;
    private String name;
    private MediaType type;
    private boolean isAdult;
}
