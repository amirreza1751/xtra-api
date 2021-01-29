package com.xtra.api.controller.admin;

import com.xtra.api.mapper.admin.CollectionMapper;
import com.xtra.api.projection.admin.collection.CollectionView;
import com.xtra.api.projection.admin.collection.CollectionInsertView;
import com.xtra.api.projection.admin.collection.CollectionSimplifiedView;
import com.xtra.api.service.admin.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/collections")
public class CollectionController {
    private final CollectionService collectionService;
    private final CollectionMapper collectionMapper;

    @Autowired
    public CollectionController(CollectionService collectionService, CollectionMapper collectionMapper) {
        this.collectionService = collectionService;
        this.collectionMapper = collectionMapper;
    }

    @GetMapping("")
    public ResponseEntity<Page<CollectionSimplifiedView>> getCollections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                         @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(new PageImpl<>(collectionService.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(collectionMapper::convertToSimpleDto).collect(Collectors.toList())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionView> getCollection(@PathVariable Long id) {
        return ResponseEntity.ok(collectionMapper.convertToDto(collectionService.findByIdOrFail(id)));
    }

    @PostMapping("")
    public ResponseEntity<CollectionView> addCollection(@RequestBody CollectionInsertView collectionInsertView) {
        return ResponseEntity.ok(collectionMapper.convertToDto(collectionService.insert(collectionMapper.convertToEntity(collectionInsertView))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CollectionView> updateCollection(@PathVariable Long id, @RequestBody CollectionInsertView collection) {
        return ResponseEntity.ok(collectionMapper.convertToDto(collectionService.updateOrFail(id, collection)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id) {
        collectionService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
