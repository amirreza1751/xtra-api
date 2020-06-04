package com.xtra.api.controller;

import com.xtra.api.model.LiveStream;
import com.xtra.api.repository.LiveStreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/live_streams")
public class LiveStreamController {
    LiveStreamRepository liveStreamRepository;

    @Autowired
    public LiveStreamController(LiveStreamRepository liveStreamRepository) {
        this.liveStreamRepository = liveStreamRepository;
    }

    @GetMapping("/")
    public List<LiveStream> getLiveStreams() {
        return liveStreamRepository.findAll();
    }

    @GetMapping("/{id}")
    public LiveStream getLiveStreamById(@PathVariable Long id) {
        return liveStreamRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
    }
}
