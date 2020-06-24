package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.Channel;
import com.xtra.api.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    ChannelRepository channelRepository;

    @Autowired
    public ChannelController(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @GetMapping("")
    public Page<Channel> getChannels(@RequestParam(defaultValue = "0", name = "page_no") int page_no, @RequestParam(defaultValue = "25", name = "page_size") int page_size) {
        Pageable page = PageRequest.of(page_no, page_size);
        return channelRepository.findAll(page);
    }

    @PostMapping("")
    public Channel addChannel(@Valid @RequestBody Channel channel) {
        return channelRepository.save(channel);
    }

    @GetMapping("/{id}")
    public Channel getChannelById(@PathVariable Long id) {
        return channelRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
    }

    @PutMapping("/{id}")
    public Channel updateChannel(@PathVariable Long id, @RequestBody Channel channel) {
        if (channelRepository.findById(id).isEmpty()) {
            throw new EntityNotFound();
        }
        channel.setId(id);
        return channelRepository.save(channel);
    }

    @DeleteMapping("/{id}")
    public void deleteStream(@PathVariable Long id) {
        channelRepository.deleteById(id);
    }

}
