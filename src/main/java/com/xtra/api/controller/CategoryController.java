package com.xtra.api.controller;

import com.xtra.api.model.MediaType;
import com.xtra.api.projection.category.CategoriesWrapper;
import com.xtra.api.projection.category.CategoryView;
import com.xtra.api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public ResponseEntity<?> getCategories(@RequestParam(required = false) MediaType type) {
        return ResponseEntity.ok(type != null ? categoryService.getCategories(type) : categoryService.getCategories());
    }

    @PostMapping("")
    public ResponseEntity<CategoryView> saveCategories(@RequestBody CategoriesWrapper wrapper) {
        categoryService.save(wrapper);
        return ResponseEntity.ok().build();
    }

}
