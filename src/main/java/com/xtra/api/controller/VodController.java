package com.xtra.api.controller;

import com.xtra.api.model.EncodeStatus;
import com.xtra.api.model.Vod;
import com.xtra.api.repository.VodRepository;
import com.xtra.api.service.VodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

@RestController
public class VodController {
    VodService vodService;

    @Autowired
    public VodController(VodService vodService) {
        this.vodService = vodService;
    }


    @GetMapping("/{id}")
    public Vod getVod(@PathVariable Long id) {
        return vodService.findByIdOrFail(id);
    }

    @PatchMapping("/{id}/encode_status")
    public ResponseEntity<?> setEncodeStatus(@PathVariable Long id, @RequestBody EncodeStatus encodeStatus) {
        vodService.setEncodeStatus(id, encodeStatus);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
