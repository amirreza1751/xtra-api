package com.xtra.api.controller.line;

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

    @GetMapping("/{id}")
    public ResponseEntity<LineView> getLineProfile(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.getById(id));
    }
}
