package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.collection.CollectionInsertView;
import com.xtra.api.projection.admin.collection.CollectionSimpleView;
import com.xtra.api.projection.admin.collection.CollectionView;
import com.xtra.api.service.admin.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collections")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class CollectionController {
    private final CollectionService collectionService;

    @Autowired
    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PreAuthorize("hasAnyAuthority({'collections_manage'})")
    @GetMapping("")
    public ResponseEntity<Page<CollectionSimpleView>> getCollections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                     @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(collectionService.listCollectionsSimple(pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'collections_manage'})")
    @GetMapping("/{id}")
    public ResponseEntity<CollectionView> getCollection(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.findViewById(id));
    }

    @PreAuthorize("hasAnyAuthority({'collections_manage'})")
    @PostMapping("")
    public ResponseEntity<CollectionView> addCollection(@RequestBody CollectionInsertView collectionInsertView) {
        return ResponseEntity.ok(collectionService.add(collectionInsertView));
    }

    @PreAuthorize("hasAnyAuthority({'collections_manage'})")
    @PatchMapping("/{id}")
    public ResponseEntity<CollectionView> updateCollection(@PathVariable Long id, @RequestBody CollectionInsertView collection) {
        return ResponseEntity.ok(collectionService.save(id, collection));
    }

    @PreAuthorize("hasAnyAuthority({'collections_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable Long id) {
        collectionService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
