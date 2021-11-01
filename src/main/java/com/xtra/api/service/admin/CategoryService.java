package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.CategoryMapper;
import com.xtra.api.model.category.Category;
import com.xtra.api.projection.admin.category.CategoryInsertView;
import com.xtra.api.projection.admin.category.CategorySummaryView;
import com.xtra.api.projection.admin.category.CategoryUpdateView;
import com.xtra.api.projection.admin.category.CategoryView;
import com.xtra.api.repository.CategoryRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class CategoryService extends CrudService<Category, Long, CategoryRepository> {
    private final CategoryMapper categoryMapper;

    @Autowired
    protected CategoryService(CategoryRepository repository, CategoryMapper categoryMapper) {
        super(repository, "Category");
        this.categoryMapper = categoryMapper;
    }

    @Override
    protected Page<Category> findWithSearch(String search, Pageable page) {
        return null;
    }

    public CategoryView getViewById(Long id) {
        return categoryMapper.convertToView(findByIdOrFail(id));
    }

    public CategorySummaryView getCategories() {
        return categoryMapper.convertToSummaryView(repository.findAll());
    }

    public CategoryView add(@Valid CategoryInsertView insertView) {
        return categoryMapper.convertToView(insert(categoryMapper.toEntity(insertView)));
    }

    public CategoryView save(Long id, @Valid CategoryUpdateView updateView) {
        var cat = findByIdOrFail(id);
        cat.setName(updateView.getName());
        cat.setAdult(updateView.isAdult());
        return categoryMapper.convertToView(insert(cat));
    }

    public CategorySummaryView order(CategorySummaryView summaryView) {
        var i = 0;
        for (var categoryView : summaryView.getChannelCategories()) {
            Category category = findByIdOrFail(categoryView.getId());
            category.setOrder(i++);
            repository.save(category);
        }

        i = 0;
        for (var categoryView : summaryView.getMovieCategories()) {
            Category category = findByIdOrFail(categoryView.getId());
            category.setOrder(i++);
            repository.save(category);
        }

        i = 0;
        for (var categoryView : summaryView.getSeriesCategories()) {
            Category category = findByIdOrFail(categoryView.getId());
            category.setOrder(i++);
            repository.save(category);
        }

        i = 0;
        for (var categoryView : summaryView.getRadioCategories()) {
            Category category = findByIdOrFail(categoryView.getId());
            category.setOrder(i++);
            repository.save(category);
        }


        return categoryMapper.convertToSummaryView(repository.findAll());
    }

}
