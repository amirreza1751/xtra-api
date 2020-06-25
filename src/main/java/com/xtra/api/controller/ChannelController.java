package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.specification.ChannelSpecification;
import com.xtra.api.specification.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    ChannelRepository channelRepository;
    ServerRepository serverRepository;

    @Autowired
    public ChannelController(ChannelRepository channelRepository, ServerRepository serverRepository) {
        this.channelRepository = channelRepository;
        this.serverRepository = serverRepository;
    }

    @GetMapping("")
    public Page<Channel> getChannels(@RequestParam(defaultValue = "0", name = "page_no") int page_no, @RequestParam(defaultValue = "25", name = "page_size") int page_size
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {

        Pageable page;
        Sort.Order order;
        if (sortBy != null) {
            if (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                order = Sort.Order.desc(sortBy);
            else
                order = Sort.Order.asc(sortBy);
            page = PageRequest.of(page_no, page_size, Sort.by(order));
        } else {
            page = PageRequest.of(page_no, page_size, Sort.by(Sort.Order.asc("id")));
        }


        if (search == null)
            return channelRepository.findAll(page);
        else {
            Optional<Server> server = serverRepository.findByName(search);
            if (server.isPresent())
                return channelRepository.findByNameOrCategoryNameOrCurrentInputUrlOrServersContains(search, search, search, server.get(), page);
            else
                return channelRepository.findByNameOrCategoryNameOrCurrentInputUrl(search, search, search, page);
        }

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
        Optional<Channel> oldChannel =channelRepository.findById(id);
        if (oldChannel.isEmpty()) {
            throw new EntityNotFound();
        }
        oldChannel.get().setServers(channel.getServers());
        return channelRepository.save(oldChannel.get());
    }

    @DeleteMapping("/{id}")
    public void deleteStream(@PathVariable Long id) {
        channelRepository.deleteById(id);
    }

}
