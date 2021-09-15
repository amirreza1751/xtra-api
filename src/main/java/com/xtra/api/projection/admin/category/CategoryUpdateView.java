package com.xtra.api.projection.admin.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryUpdateView {
    @NotBlank
    private String name;
    private boolean isAdult = false;
}
