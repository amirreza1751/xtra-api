package com.xtra.api.controller.admin;

import com.xtra.api.model.vod.Video;
import com.xtra.api.service.admin.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    //Play a Video
    @GetMapping("/play/{line_token}/{video_token}")
    public void playVideo(@PathVariable String video_token, @PathVariable String line_token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String videoLink = videoService.playVideo(video_token, line_token, request);
        response.sendRedirect(videoLink);
    }
}
