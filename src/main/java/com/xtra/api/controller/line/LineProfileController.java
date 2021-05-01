package com.xtra.api.controller.line;

import com.xtra.api.projection.line.line.LineInsertView;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.service.line.LineLineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("lines/profile")
public class LineProfileController {
    private final LineLineServiceImpl lineService;

    @Autowired
    public LineProfileController(LineLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @GetMapping("")
    public ResponseEntity<LineView> getLineProfile() {
        return ResponseEntity.ok(lineService.getProfile());
    }

    @PatchMapping("")
    public ResponseEntity<LineView> updateLineProfile(@RequestBody LineInsertView insertView) {
        return ResponseEntity.ok(lineService.updateProfile(insertView));
    }
}
