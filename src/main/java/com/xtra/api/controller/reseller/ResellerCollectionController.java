package com.xtra.api.controller.reseller;

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
@RequestMapping("/reseller/collections")
@PreAuthorize("hasAnyRole({'RESELLER'})")
public class ResellerCollectionController {
    private final CollectionService collectionService;

    @Autowired
    public ResellerCollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("")
    public ResponseEntity<Page<CollectionSimpleView>> getCollections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                     @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(collectionService.listCollectionsSimple(pageNo, pageSize, sortBy, sortDir));
    }
}
