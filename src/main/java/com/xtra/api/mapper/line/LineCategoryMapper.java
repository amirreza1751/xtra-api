package com.xtra.api.mapper.line;

import com.xtra.api.model.category.Category;
import com.xtra.api.projection.line.CategoryView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class LineCategoryMapper {

    public abstract CategoryView convertoToView(Category category);
}
