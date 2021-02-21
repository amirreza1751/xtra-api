package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.category.CategoryInsertView;
import com.xtra.api.projection.admin.category.CategoryView;
import com.xtra.api.service.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    public ResponseEntity<List<CategoryView>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @PostMapping("")
    public ResponseEntity<CategoryView> saveCategory(@RequestBody CategoryInsertView insertView) {
        return ResponseEntity.ok(categoryService.save(insertView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
