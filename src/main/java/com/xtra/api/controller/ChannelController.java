package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.*;
import com.xtra.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Optional;

import static com.xtra.api.util.Utilities.*;
import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    ChannelRepository channelRepository;
    ServerRepository serverRepository;
    StreamRepository streamRepository;
    ProgressInfoRepository progressInfoRepository;
    StreamInfoRepository streamInfoRepository;

    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    public ChannelController(ChannelRepository channelRepository, ServerRepository serverRepository, StreamRepository streamRepository, ProgressInfoRepository progressInfoRepository, StreamInfoRepository streamInfoRepository) {
        this.channelRepository = channelRepository;
        this.serverRepository = serverRepository;
        this.streamRepository = streamRepository;
        this.progressInfoRepository = progressInfoRepository;
        this.streamInfoRepository = streamInfoRepository;
    }

    // Stream CRUD
    @GetMapping("")
    public Page<Channel> getChannels(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {

        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);

        if (search == null)
            return channelRepository.findAll(page);
        else {
            search = wrapSearchString(search);
            Optional<Server> server = serverRepository.findByName(search);
            if (server.isPresent())
                return channelRepository.findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLikeOrServersContains(search, search, search, server.get(), page);
            else
                return channelRepository.findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLike(search, search, search, page);
        }

    }

    @PostMapping(value = {"", "/{restart}"})
    public Channel addChannel(@Valid @RequestBody Channel channel, @PathVariable(required = false) boolean restart) {
        String token;
        do {
            token = generateRandomString(8, 12, false);
        } while (streamRepository.getByStreamToken(token).isPresent());
        channel.setStreamToken(token);
        Channel ch = channelRepository.save(channel);
        if (restart) {
            var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/start/" + ch.getId(), String.class);
        }

        return ch;
    }

    @GetMapping("/{id}")
    public Channel getChannelById(@PathVariable Long id) {
        return channelRepository.findById(id).orElseThrow(() -> new RuntimeException("not found"));
    }

    @PatchMapping(value = {"/{id}", "/{id}/{restart}"})
    public Channel updateChannel(@PathVariable Long id, @RequestBody Channel channel, @PathVariable(required = false) boolean restart) {
        Optional<Channel> result = channelRepository.findById(id);
        if (result.isEmpty()) {
            throw new EntityNotFound();
        }
        Channel oldChannel = result.get();
        copyProperties(channel, oldChannel, "id", "currentInput");
        if (restart)
            new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/restart/" + channel.getId(), String.class);
        return channelRepository.save(oldChannel);
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
            var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/start/" + id, String.class);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        } else
            throw new EntityNotFound();
    }

    @GetMapping("/stop/{id}")
    public ResponseEntity<String> stopChannel(@PathVariable Long id) {
        Optional<Channel> channelById = channelRepository.findById(id);
        if (channelById.isPresent()) {
            Channel channel = channelById.get();
            var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/stop/" + channel.getId(), String.class);
            channel.setStreamInfo(null);
            channel.setProgressInfo(null);
            channelRepository.save(channel);
            //progressInfoRepository.delete(channel.getProgressInfo());
            //streamInfoRepository.delete(channel.getStreamInfo());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        } else
            throw new EntityNotFound();
    }

    @GetMapping("/restart/{id}")
    public ResponseEntity<String> restartChannel(@PathVariable Long id) {
        Optional<Channel> channel = channelRepository.findById(id);
        if (channel.isPresent()) {
            Stream stream = channel.get();
            var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/restart/" + stream.getId(), String.class);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        } else
            throw new EntityNotFound();
    }
}
