package com.xtra.api.repository;

import com.xtra.api.model.Category;
import com.xtra.api.model.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findAllByTypeOrderByOrder(MediaType type);

    List<Category> findAllByType(MediaType type);

    Optional<Category> findByNameAndType(String name, MediaType type);
}
