package com.xtra.api.controller.system;

import com.xtra.api.model.vod.Video;
import com.xtra.api.projection.admin.video.EncodeResponse;
import com.xtra.api.projection.admin.video.VideoView;
import com.xtra.api.service.admin.VideoService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<VideoView> getVideoByToken(@PathVariable("video_token") String videoToken) {
        return ResponseEntity.ok(videoService.getByToken(videoToken));
    }

    @PatchMapping("/{id}/encode_status")
    public ResponseEntity<?> updateEncodeStatus(@PathVariable Long id, @RequestHeader(value = "server_token") String serverToken,
                                                @RequestBody EncodeResponse encodeResponse) {
        videoService.updateEncodeStatus(id, serverToken, encodeResponse);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
