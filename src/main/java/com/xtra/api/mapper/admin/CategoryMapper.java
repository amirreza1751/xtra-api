package com.xtra.api.mapper.admin;

import com.xtra.api.model.Category;
import com.xtra.api.model.MediaType;
import com.xtra.api.projection.category.CategoriesWrapper;
import com.xtra.api.projection.category.CategoryView;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

    public abstract CategoryView convertToView(Category category);

    public List<Category> convertToChannelCategories(CategoriesWrapper wrapper) {
        List<Category> categories = new ArrayList<>();
        int i = 0;
        for (var name : emptyIfNull(wrapper.getChannelCategories())) {
            categories.add(new Category(name, MediaType.CHANNEL, i++));
        }
        return categories;
    }

    public List<Category> convertToRadioCategories(CategoriesWrapper wrapper) {
        List<Category> categories = new ArrayList<>();
        int i = 0;
        for (var name : emptyIfNull(wrapper.getRadioCategories())) {
            categories.add(new Category(name, MediaType.RADIO, i++));
        }
        return categories;
    }

    public List<Category> convertToMovieCategories(CategoriesWrapper wrapper) {
        List<Category> categories = new ArrayList<>();
        int i = 0;
        for (var name : emptyIfNull(wrapper.getMovieCategories())) {
            categories.add(new Category(name, MediaType.MOVIE, i++));
        }
        return categories;
    }

    public List<Category> convertToSeriesCategories(CategoriesWrapper wrapper) {
        List<Category> categories = new ArrayList<>();
        int i = 0;
        for (var name : emptyIfNull(wrapper.getSeriesCategories())) {
            categories.add(new Category(name, MediaType.SERIES, i++));
        }
        return categories;
    }
}
