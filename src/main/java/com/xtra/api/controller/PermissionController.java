package com.xtra.api.controller;

import com.xtra.api.facade.PermissionFacade;
import com.xtra.api.model.Permission;
import com.xtra.api.projection.PermissionDTO;
import com.xtra.api.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    private final PermissionService permissionService;
    private final PermissionFacade permissionFacade;

    @Autowired
    public PermissionController(PermissionService permissionService, PermissionFacade permissionFacade) {
        this.permissionService = permissionService;
        this.permissionFacade = permissionFacade;
    }

    @GetMapping("")
    public ResponseEntity<Page<PermissionDTO>> getPermissions(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = permissionService.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(new PageImpl<>(result.stream().map(permissionFacade::convertToDTO).collect(Collectors.toList())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable String id) {
        return ResponseEntity.ok(permissionService.findByIdOrFail(id));
    }


}
