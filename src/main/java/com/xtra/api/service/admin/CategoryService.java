package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.CategoryMapper;
import com.xtra.api.model.Category;
import com.xtra.api.projection.admin.category.CategoryInsertView;
import com.xtra.api.projection.admin.category.CategoryView;
import com.xtra.api.repository.CategoryRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<CategoryView> getCategories() {
        return repository.findAll().stream().map(categoryMapper::convertToView).collect(Collectors.toList());
    }

    public CategoryView save(CategoryInsertView insertView) {
        return categoryMapper.convertToView(insert(categoryMapper.toEntity(insertView)));
    }

    public void deleteById(String id) {
    }
}
