package com.xtra.api.controller.admin;

import com.xtra.api.mapper.admin.AdminMapper;
import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.admin.AdminBatchDeleteView;
import com.xtra.api.projection.admin.user.admin.AdminBatchInsertView;
import com.xtra.api.projection.admin.user.admin.AdminInsertView;
import com.xtra.api.projection.admin.user.admin.AdminView;
import com.xtra.api.service.admin.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(adminService.findAll(search, pageNo, pageSize, sortBy, sortDir).map(adminMapper::convertToView));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserSimpleView>> getAdminsSimpleList(@RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminService.getAdminList(search));
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

    @PatchMapping("/batches")
    public ResponseEntity<?> updateAdminBatch(@RequestBody AdminBatchInsertView admins) {
        adminService.saveAll(admins);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        adminService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/batches")
    public ResponseEntity<?> deleteAdmins(@RequestBody AdminBatchDeleteView batchDelete) {
        adminService.deleteAll(batchDelete);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
