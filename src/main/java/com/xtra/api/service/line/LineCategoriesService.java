package com.xtra.api.service.line;

import com.xtra.api.mapper.line.LineCategoryMapper;
import com.xtra.api.model.MediaType;
import com.xtra.api.model.category.Category;
import com.xtra.api.projection.line.CategoryView;
import com.xtra.api.repository.CategoryRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class LineCategoriesService extends CrudService<Category, Long, CategoryRepository> {

    LineCategoryMapper categoryMapper;

    protected LineCategoriesService(CategoryRepository repository, LineCategoryMapper categoryMapper) {
        super(repository, "Category");
        this.categoryMapper = categoryMapper;
    }

    @Override
    protected Page<Category> findWithSearch(String search, Pageable page) {
        return null;
    }

    public List<CategoryView> getCategories(MediaType type) {
        return repository.findAllByType(type).stream().map(this.categoryMapper::convertoToView).collect(Collectors.toList());
    }
}
