package com.xtra.api.controller.line;

import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.service.line.LineLineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
@RequestMapping("lines/download")
public class DownloadLineController {
    private final LineLineServiceImpl lineService;

    @Autowired
    public DownloadLineController(LineLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @GetMapping("")
    public ResponseEntity<String> downloadLine() {
        Map<String, String> data = lineService.downloadLine();
        HttpHeaders responseHeaders = new HttpHeaders();
        return ResponseEntity.ok()
                .headers(responseHeaders).contentType(MediaType.valueOf("application/x-mpegurl"))
                .headers(responseHeaders).contentLength(Long.parseLong(String.valueOf(data.get("playlist").length())))
                .headers(responseHeaders).cacheControl(CacheControl.noCache())
                .headers(responseHeaders).cacheControl(CacheControl.noStore())
                .header("Content-Disposition", "inline; filename=" + "\"" + data.get("fileName") + "\"")
                .body(data.get("playlist"));
    }
}
