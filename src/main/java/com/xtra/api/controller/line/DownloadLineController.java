package com.xtra.api.controller.line;

import com.xtra.api.service.line.LineLineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("lines/download")
@PreAuthorize("hasAnyRole({'LINE'})")
public class DownloadLineController {
    private final LineLineServiceImpl lineService;

    @Autowired
    public DownloadLineController(LineLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @GetMapping("")
    public ResponseEntity<String> downloadLine() {
        return lineService.downloadLinePlaylist();
    }
}
