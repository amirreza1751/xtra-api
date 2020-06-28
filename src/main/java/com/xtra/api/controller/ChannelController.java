package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.model.Stream;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    ChannelRepository channelRepository;
    ServerRepository serverRepository;
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    public ChannelController(ChannelRepository channelRepository, ServerRepository serverRepository) {
        this.channelRepository = channelRepository;
        this.serverRepository = serverRepository;
    }

    // Stream CRUD
    @GetMapping("")
    public Page<Channel> getChannels(@RequestParam(defaultValue = "0" ) int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {

        Pageable page;
        Sort.Order order;
        if (sortBy != null) {
            if (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                order = Sort.Order.desc(sortBy);
            else
                order = Sort.Order.asc(sortBy);
            page = PageRequest.of(pageNo, pageSize, Sort.by(order));
        } else {
            page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc("id")));
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
        Optional<Channel> oldChannel = channelRepository.findById(id);
        if (oldChannel.isEmpty()) {
            throw new EntityNotFound();
        }
        oldChannel.get().setServers(channel.getServers());
        return channelRepository.save(oldChannel.get());
    }

    @DeleteMapping("/{id}")
    public void deleteChannel(@PathVariable Long id) {
        channelRepository.deleteById(id);
    }

    // Stream Operations
    @GetMapping("/start/{id}")
    public ResponseEntity<String> startChannel(@PathVariable Long id) {
        Optional<Channel> channel = channelRepository.findById(id);
        if (channel.isPresent()) {
            Stream stream = channel.get();
            var result = new RestTemplate().postForObject(corePath + ":" + corePort + "/streams/start/", stream, String.class);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        } else
            throw new EntityNotFound();
    }

    @GetMapping("/stop/{id}")
    public ResponseEntity<String> stopChannel(@PathVariable Long id) {
        Optional<Channel> channel = channelRepository.findById(id);
        if (channel.isPresent()) {
            Stream stream = channel.get();
            var result = new RestTemplate().postForObject(corePath + ":" + corePort + "/streams/stop/", stream, String.class);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        } else
            throw new EntityNotFound();
    }
}
