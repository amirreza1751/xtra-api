package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.epg.EpgInsertView;
import com.xtra.api.projection.admin.epg.EpgSimpleView;
import com.xtra.api.projection.admin.epg.EpgView;
import com.xtra.api.service.admin.EpgFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/epg")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class EpgController {

    private final EpgFileService epgFileService;

    @Autowired
    public EpgController(EpgFileService epgFileService){
        this.epgFileService = epgFileService;
    }

    @PreAuthorize("hasAnyAuthority({'epg_manage'})")
    @GetMapping("")
    public ResponseEntity<Page<EpgSimpleView>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(epgFileService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'epg_manage'})")
    @GetMapping("/{id}")
    public ResponseEntity<EpgView> getEpgFile(@PathVariable Long id) {
        return ResponseEntity.ok(epgFileService.getById(id));
    }

    @PreAuthorize("hasAnyAuthority({'epg_manage'})")
    @PostMapping("")
    public ResponseEntity<EpgView> addEpgFile(@RequestBody EpgInsertView insertView) {
        return ResponseEntity.ok(epgFileService.add(insertView));
    }

    @PreAuthorize("hasAnyAuthority({'epg_manage'})")
    @PatchMapping("/{id}")
    public ResponseEntity<EpgView> updateEpgFile(@PathVariable Long id, @RequestBody EpgInsertView insertView) {
        return ResponseEntity.ok(epgFileService.updateEpgFile(id, insertView));
    }

    @PreAuthorize("hasAnyAuthority({'epg_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEpgFile(@PathVariable Long id) {
        epgFileService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAnyAuthority({'epg_manage'})")
    @GetMapping("/sync-all")
    public ResponseEntity<?> syncEpgNow(){
        epgFileService.syncAllEpg();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
