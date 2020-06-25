package com.xtra.api.controller;

import com.xtra.api.model.Server;
import com.xtra.api.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/servers")
public class ServerController {
    ServerRepository serverRepository;

    @Autowired
    public ServerController(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @GetMapping("")
    public Page<Server> getServers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        Pageable page = PageRequest.of(pageNo, pageSize);
        return serverRepository.findAll(page);
    }

    @PostMapping("")
    public Server addServer(@Valid @RequestBody Server server) {
        return serverRepository.save(server);
    }
}
