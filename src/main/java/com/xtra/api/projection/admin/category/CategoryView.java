package com.xtra.api.projection.admin.category;

import com.xtra.api.model.MediaType;
import lombok.Data;

@Data
public class CategoryView {
    private Long id;
    private String name;
    private MediaType type;
    private boolean isAdult;
}
