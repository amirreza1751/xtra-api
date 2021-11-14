package com.xtra.api.controller.line;

import com.xtra.api.service.admin.ChannelService;
import com.xtra.api.service.admin.VideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("play/")
public class PlayController {
    private final ChannelService channelService;
    private final VideoService videoService;

    public PlayController(ChannelService channelService, VideoService videoService) {
        this.channelService = channelService;
        this.videoService = videoService;
    }

    //Play a Channel
    @GetMapping("channel/{line_token}/{stream_token}")
    public void playChannel(@PathVariable String stream_token, @PathVariable String line_token, HttpServletRequest request, HttpServletResponse response) throws IOException {
         channelService.playChannel(stream_token, line_token, request, response);
//        response.sendRedirect(channelLink);
    }

    //Play a Video
    @GetMapping("video/{line_token}/{video_token}")
    public void playVideo(@PathVariable String video_token, @PathVariable String line_token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String videoLink = videoService.playVideo(video_token, line_token, request);
        response.sendRedirect(videoLink);
    }
}
