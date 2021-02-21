package com.xtra.api.mapper.admin;

import com.xtra.api.model.Category;
import com.xtra.api.projection.admin.category.CategoryInsertView;
import com.xtra.api.projection.admin.category.CategoryView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryView convertToView(Category category);

    Category toEntity(CategoryInsertView insertView);
}
