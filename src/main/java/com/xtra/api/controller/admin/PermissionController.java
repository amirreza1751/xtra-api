package com.xtra.api.controller.admin;

import com.xtra.api.mapper.admin.PermissionMapper;
import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.PermissionView;
import com.xtra.api.service.admin.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    @Autowired
    public PermissionController(PermissionService permissionService, PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }

    @GetMapping("")
    public ResponseEntity<List<PermissionView>> getPermissions(@RequestParam(value = "user_type", required = false) UserType userType) {
        var result = permissionService.getPermissions(userType);
        return ResponseEntity.ok(result.stream().map(permissionMapper::convertToDto).collect(Collectors.toList()));
    }


}
