package com.xtra.api.controller;

import com.xtra.api.model.Collection;
import com.xtra.api.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/collections")
public class CollectionController {
    private final CollectionService collectionService;

    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("")
    public ResponseEntity<Page<Collection>> getCollections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                           @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(collectionService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollection(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.findByIdOrFail(id));
    }

    @PostMapping("")
    public ResponseEntity<Collection> addCollection(@RequestBody @Valid Collection collection) {
        return ResponseEntity.ok(collectionService.add(collection));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(@PathVariable Long id, @RequestBody @Valid Collection collection) {
        return ResponseEntity.ok(collectionService.updateOrFail(id, collection));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id) {
        collectionService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
