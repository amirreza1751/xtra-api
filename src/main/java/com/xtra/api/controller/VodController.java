package com.xtra.api.controller;

import com.xtra.api.model.Vod;
import com.xtra.api.repository.VodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VodController {
    VodRepository vodRepository;

    @Autowired
    public VodController(VodRepository vodRepository) {
        this.vodRepository = vodRepository;
    }


    @GetMapping("/{id}")
    public Vod getVod(@PathVariable Long id) {
        return vodRepository.findById(id).orElseThrow(() -> new RuntimeException("Vod not found!"));
    }


}
