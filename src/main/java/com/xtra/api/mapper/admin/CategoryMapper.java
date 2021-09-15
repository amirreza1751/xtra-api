package com.xtra.api.mapper.admin;

import com.xtra.api.model.MediaType;
import com.xtra.api.model.category.Category;
import com.xtra.api.projection.EntityListItem;
import com.xtra.api.projection.admin.category.CategoryInsertView;
import com.xtra.api.projection.admin.category.CategorySummaryView;
import com.xtra.api.projection.admin.category.CategoryView;
import org.mapstruct.Mapper;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {


    public abstract CategoryView convertToView(Category category);

    /*@AfterMapping
    private CategoryView addContents(@MappingTarget CategoryView categoryView, final Category category) {

    }*/

    public abstract Category toEntity(CategoryInsertView insertView);

    public CategorySummaryView convertToSummaryView(List<Category> categories) {
        var summaryView = new CategorySummaryView();
        summaryView.setChannelCategories(categories.stream().filter(category -> category.getType().equals(MediaType.CHANNEL))
                .sorted(Comparator.comparing(Category::getOrder)).map(category -> new EntityListItem(category.getId(), category.getName())).collect(Collectors.toList()));
        summaryView.setRadioCategories(categories.stream().filter(category -> category.getType().equals(MediaType.RADIO))
                .sorted(Comparator.comparing(Category::getOrder)).map(category -> new EntityListItem(category.getId(), category.getName())).collect(Collectors.toList()));
        summaryView.setMovieCategories(categories.stream().filter(category -> category.getType().equals(MediaType.MOVIE))
                .sorted(Comparator.comparing(Category::getOrder)).map(category -> new EntityListItem(category.getId(), category.getName())).collect(Collectors.toList()));
        summaryView.setSeriesCategories(categories.stream().filter(category -> category.getType().equals(MediaType.SERIES))
                .sorted(Comparator.comparing(Category::getOrder)).map(category -> new EntityListItem(category.getId(), category.getName())).collect(Collectors.toList()));
        return summaryView;
    }
}
