package com.xtra.api.controller.admin;

import com.xtra.api.mapper.admin.AdminMapper;
import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.admin.AdminInsertView;
import com.xtra.api.projection.admin.user.admin.AdminView;
import com.xtra.api.service.admin.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;
    private final AdminMapper adminMapper;

    public AdminController(AdminService adminService, AdminMapper adminMapper) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
    }

    @GetMapping("")
    public ResponseEntity<Page<AdminView>> getAdmins(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                     @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(new PageImpl<>(adminService.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(adminMapper::convertToView).collect(Collectors.toList())));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<UserSimpleView>> getAdminsSimpleList(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                    @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(new PageImpl<>(adminService.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(adminMapper::convertToSimpleView).collect(Collectors.toList())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminView> getAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(adminMapper.convertToView(adminService.findByIdOrFail(id)));
    }

    @PostMapping("")
    public ResponseEntity<AdminView> addAdmin(@RequestBody AdminInsertView adminInsertView) {
        return ResponseEntity.ok(adminService.add(adminInsertView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminView> updateAdmin(@PathVariable Long id, @RequestBody AdminInsertView admin) {
        return ResponseEntity.ok(adminService.save(id, admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        adminService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
