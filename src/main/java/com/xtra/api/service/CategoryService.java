package com.xtra.api.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xtra.api.mapper.CategoryMapper;
import com.xtra.api.model.Category;
import com.xtra.api.model.MediaType;
import com.xtra.api.projection.category.CategoriesWrapper;
import com.xtra.api.projection.category.CategoryView;
import com.xtra.api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService extends CrudService<Category, String, CategoryRepository> {
    private final CategoryMapper categoryMapper;

    @Autowired
    protected CategoryService(CategoryRepository repository, CategoryMapper categoryMapper) {
        super(repository, Category.class);
        this.categoryMapper = categoryMapper;
    }

    @Override
    protected Page<Category> findWithSearch(Pageable page, String search) {
        return null;
    }

    public List<String> getCategories(MediaType type) {
        return repository.findAllByTypeOrderByOrder(type).stream().map(Category::getName).collect(Collectors.toList());
    }

    public CategoriesWrapper getCategories() {
        CategoriesWrapper cw = new CategoriesWrapper();
        cw.setChannelCategories(getCategories(MediaType.CHANNEL));
        cw.setRadioCategories(getCategories(MediaType.RADIO));
        cw.setMovieCategories(getCategories(MediaType.MOVIE));
        cw.setSeriesCategories(getCategories(MediaType.SERIES));
        return cw;
    }

    public void save(CategoriesWrapper wrapper) {
        //todo update categories
        List<Category> categories = new ArrayList<>();
        List<Category> toDelete = new ArrayList<>();
        List<Category> newChannelCats = categoryMapper.convertToChannelCategories(wrapper);
        List<Category> newRadioCats = categoryMapper.convertToRadioCategories(wrapper);
        List<Category> newMovieCats = categoryMapper.convertToMovieCategories(wrapper);
        List<Category> newSeriesCats = categoryMapper.convertToSeriesCategories(wrapper);

        toDelete.addAll(Sets.difference(new HashSet<>(repository.findAllByType(MediaType.CHANNEL)), new HashSet<>(newChannelCats)));
        toDelete.addAll(Sets.difference(new HashSet<>(repository.findAllByType(MediaType.RADIO)), new HashSet<>(newRadioCats)));
        toDelete.addAll(Sets.difference(new HashSet<>(repository.findAllByType(MediaType.MOVIE)), new HashSet<>(newMovieCats)));
        toDelete.addAll(Sets.difference(new HashSet<>(repository.findAllByType(MediaType.SERIES)), new HashSet<>(newSeriesCats)));
        repository.deleteAll(toDelete);

        for (var cat : newChannelCats) {
            var tmpCat = repository.findByNameAndType(cat.getName(), MediaType.CHANNEL).orElse(cat);
            tmpCat.setOrder(cat.getOrder());
            categories.add(tmpCat);
        }
        for (var cat : newRadioCats) {
            var tmpCat = repository.findByNameAndType(cat.getName(), MediaType.RADIO).orElse(cat);
            tmpCat.setOrder(cat.getOrder());
            categories.add(tmpCat);
        }
        for (var cat : newMovieCats) {
            var tmpCat = repository.findByNameAndType(cat.getName(), MediaType.MOVIE).orElse(cat);
            tmpCat.setOrder(cat.getOrder());
            categories.add(tmpCat);
        }
        for (var cat : newSeriesCats) {
            var tmpCat = repository.findByNameAndType(cat.getName(), MediaType.SERIES).orElse(cat);
            tmpCat.setOrder(cat.getOrder());
            categories.add(tmpCat);
        }
        repository.saveAll(categories);
    }
}
