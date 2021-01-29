package com.xtra.api.controller.admin;

import com.xtra.api.mapper.admin.RoleMapper;
import com.xtra.api.projection.admin.role.RoleInsertView;
import com.xtra.api.projection.admin.role.RoleView;
import com.xtra.api.service.admin.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Autowired
    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping("")
    public ResponseEntity<Page<RoleView>> getRoles(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = roleService.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(new PageImpl<>(result.stream().map(roleMapper::convertToDto).collect(Collectors.toList())));
    }

    @PostMapping("")
    public ResponseEntity<RoleView> addRole(@RequestBody RoleInsertView roleView) {
        return ResponseEntity.ok(roleMapper.convertToDto(roleService.insert(roleMapper.convertToEntity(roleView))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleView> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleMapper.convertToDto(roleService.findByIdOrFail(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoleView> updateRole(@PathVariable Long id, @RequestBody RoleInsertView roleView) {
        roleView.setId(id);
        var result = roleService.updateOrFail(id, roleMapper.convertToEntity(roleView));
        return ResponseEntity.ok(roleMapper.convertToDto(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
