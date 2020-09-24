package com.xtra.api.controller;

import com.xtra.api.facade.PermissionFacade;
import com.xtra.api.model.Permission;
import com.xtra.api.model.UserType;
import com.xtra.api.projection.PermissionDTO;
import com.xtra.api.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<List<PermissionDTO>> getPermissions(@RequestParam(value = "user_type", required = false) UserType userType) {
        var result = permissionService.getPermissions(userType);
        return ResponseEntity.ok(result.stream().map(permissionFacade::convertToDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable String id) {
        return ResponseEntity.ok(permissionService.findByIdOrFail(id));
    }


}
