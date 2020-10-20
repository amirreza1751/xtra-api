package com.xtra.api.controller;

import com.xtra.api.mapper.CollectionMapper;
import com.xtra.api.model.Collection;
import com.xtra.api.projection.CollectionDto;
import com.xtra.api.projection.CollectionInsertDto;
import com.xtra.api.projection.CollectionSimplifiedDto;
import com.xtra.api.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResponseEntity<Page<CollectionSimplifiedDto>> getCollections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                        @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(new PageImpl<>(collectionService.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(collectionMapper::convertToSimpleDto).collect(Collectors.toList())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionDto> getCollection(@PathVariable Long id) {
        return ResponseEntity.ok(collectionMapper.convertToDto(collectionService.findByIdOrFail(id)));
    }

    @PostMapping("")
    public ResponseEntity<CollectionDto> addCollection(@RequestBody CollectionInsertDto collectionInsertDto) {
        return ResponseEntity.ok(collectionMapper.convertToDto(collectionService.insert(collectionMapper.convertToEntity(collectionInsertDto))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CollectionDto> updateCollection(@PathVariable Long id, @RequestBody CollectionInsertDto collection) {
        return ResponseEntity.ok(collectionMapper.convertToDto(collectionService.updateOrFail(id, collection)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id) {
        collectionService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
