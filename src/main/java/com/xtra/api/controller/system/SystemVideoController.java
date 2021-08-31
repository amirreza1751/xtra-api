package com.xtra.api.controller.system;

import com.xtra.api.model.vod.Video;
import com.xtra.api.service.admin.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/videos")
public class SystemVideoController {
    private final VideoService videoService;

    public SystemVideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("{video_token}")
    public ResponseEntity<Video> getVideoByToken(@PathVariable("video_token") String videoToken) {
        return ResponseEntity.ok(videoService.getByToken(videoToken));
    }

    @PatchMapping("{id}")
    public ResponseEntity<Video> updateVideoPath(@PathVariable Long id, @RequestBody String videoPath) {
        videoService.updateVideoPath(id, videoPath);
        return ResponseEntity.ok().build();
    }
}
