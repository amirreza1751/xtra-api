package com.xtra.api.projection.admin.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryInsertView {
    @NotBlank(message = "category name can not be empty")
    private String name;
}
