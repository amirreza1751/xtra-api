package com.xtra.api.controller;

import com.xtra.api.mapper.ResellerMapper;
import com.xtra.api.projection.user.UserSimpleView;
import com.xtra.api.projection.user.reseller.ResellerInsertView;
import com.xtra.api.projection.user.reseller.ResellerView;
import com.xtra.api.service.ResellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/resellers")
public class ResellerController {
    ResellerService resellerService;
    ResellerMapper resellerMapper;

    @Autowired
    public ResellerController(ResellerService resellerService, ResellerMapper resellerMapper) {
        this.resellerService = resellerService;
        this.resellerMapper = resellerMapper;
    }

    @GetMapping("")
    public ResponseEntity<Page<ResellerView>> getResellers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                           @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(new PageImpl<>(resellerService.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(resellerMapper::convertToView).collect(Collectors.toList())));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<UserSimpleView>> getResellersSimpleList(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                       @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(new PageImpl<>(resellerService.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(resellerMapper::convertToSimpleView).collect(Collectors.toList())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResellerView> getReseller(@PathVariable Long id) {
        return ResponseEntity.ok(resellerMapper.convertToView(resellerService.findByIdOrFail(id)));
    }

    @PostMapping("")
    public ResponseEntity<ResellerView> addReseller(@RequestBody ResellerInsertView resellerInsertView) {
        return ResponseEntity.ok(resellerService.add(resellerInsertView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResellerView> updateReseller(@PathVariable Long id, @RequestBody ResellerInsertView reseller) {
        return ResponseEntity.ok(resellerService.save(id, reseller));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReseller(@PathVariable Long id) {
        resellerService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
