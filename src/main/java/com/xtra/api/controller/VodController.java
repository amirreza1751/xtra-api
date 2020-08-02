package com.xtra.api.controller;

import com.xtra.api.repository.VodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VodController {
    VodRepository vodRepository;

    @Autowired
    public VodController(VodRepository vodRepository) {
        this.vodRepository = vodRepository;
    }


}
