package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.category.CategoryInsertView;
import com.xtra.api.projection.admin.category.CategorySummaryView;
import com.xtra.api.projection.admin.category.CategoryUpdateView;
import com.xtra.api.projection.admin.category.CategoryView;
import com.xtra.api.service.admin.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class CategoryController {
    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasAnyAuthority({'category_manage'})")
    @GetMapping("")
    public ResponseEntity<CategorySummaryView> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }

    @PreAuthorize("hasAnyAuthority({'category_manage'})")
    @PostMapping("")
    public ResponseEntity<CategoryView> addCategory(@RequestBody CategoryInsertView insertView) {
        return ResponseEntity.ok(categoryService.add(insertView));
    }

    @PreAuthorize("hasAnyAuthority({'category_manage'})")
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryView> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateView updateView) {
        return ResponseEntity.ok(categoryService.save(id, updateView));
    }

    @PreAuthorize("hasAnyAuthority({'category_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteOrFail(id);
        return ResponseEntity.ok().build();
    }
}
