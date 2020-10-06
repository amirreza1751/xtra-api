package com.xtra.api.controller;

import com.xtra.api.projection.RoleDto;
import com.xtra.api.facade.RoleFacade;
import com.xtra.api.service.RoleService;
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
    private final RoleFacade roleFacade;

    @Autowired
    public RoleController(RoleService roleService, RoleFacade roleFacade) {
        this.roleService = roleService;
        this.roleFacade = roleFacade;
    }

    @GetMapping("")
    public ResponseEntity<Page<RoleDto>> getRoles(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = roleService.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(new PageImpl<>(result.stream().map(roleFacade::convertToDto).collect(Collectors.toList())));
    }

    @PostMapping("")
    public ResponseEntity<RoleDto> addRole(@RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleFacade.convertToDto(roleService.add(roleFacade.convertToEntity(roleDto))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleFacade.convertToDto(roleService.findByIdOrFail(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id, @RequestBody RoleDto roleDTO) {
        roleDTO.setId(id);
        var result = roleService.updateOrFail(id, roleFacade.convertToEntity(roleDTO));
        return ResponseEntity.ok(roleFacade.convertToDto(result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
