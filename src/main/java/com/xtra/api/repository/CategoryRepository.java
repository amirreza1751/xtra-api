package com.xtra.api.repository;

import com.xtra.api.model.MediaType;
import com.xtra.api.model.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByType(MediaType type);

    List<Category> findAllByTypeIn(List<MediaType> types);
}
