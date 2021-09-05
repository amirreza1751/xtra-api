package com.xtra.api.controller.admin;

import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.role.RoleInsertView;
import com.xtra.api.projection.admin.role.RoleListItem;
import com.xtra.api.projection.admin.role.RoleView;
import com.xtra.api.service.admin.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasAnyAuthority({'roles_manage'})")
    @GetMapping("")
    public ResponseEntity<Page<RoleView>> getRoles(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = roleService.getRoles(search, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAnyAuthority({'roles_manage'})")
    @GetMapping("/list/{type}")
    public ResponseEntity<List<RoleListItem>> getRoleList(@PathVariable(value = "type", required = false) UserType type) {
        return ResponseEntity.ok(roleService.getRoleList(type));
    }

    @PreAuthorize("hasAnyAuthority({'roles_manage'})")
    @PostMapping("")
    public ResponseEntity<RoleView> addRole(@RequestBody RoleInsertView roleView) {
        return ResponseEntity.ok(roleService.add(roleView));
    }

    @PreAuthorize("hasAnyAuthority({'roles_manage'})")
    @GetMapping("/{id}")
    public ResponseEntity<RoleView> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PreAuthorize("hasAnyAuthority({'roles_manage'})")
    @PatchMapping("/{id}")
    public ResponseEntity<RoleView> updateRole(@PathVariable Long id, @RequestBody RoleInsertView roleView) {
        return ResponseEntity.ok(roleService.updateOrFail(id, roleView));
    }

    @PreAuthorize("hasAnyAuthority({'roles_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
