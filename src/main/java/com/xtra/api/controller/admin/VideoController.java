package com.xtra.api.controller.admin;

import com.xtra.api.model.Video;
import com.xtra.api.service.admin.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService){
        this.videoService = videoService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody Video video) {
        return ResponseEntity.ok(videoService.updateVideo(id, video));
    }

    @GetMapping("/token/{video_token}")
    public ResponseEntity<Video> getVideoByToken(@PathVariable("video_token") String videoToken) {
        return ResponseEntity.ok(videoService.getByToken(videoToken));
    }
}
