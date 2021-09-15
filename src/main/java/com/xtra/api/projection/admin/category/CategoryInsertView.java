package com.xtra.api.projection.admin.category;

import com.xtra.api.model.MediaType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryInsertView extends CategoryUpdateView {
    @NotNull
    private MediaType type;
}
