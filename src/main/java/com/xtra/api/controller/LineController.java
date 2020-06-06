package com.xtra.api.controller;

import com.xtra.api.model.Line;
import com.xtra.api.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    LineRepository lineRepository;

    @Autowired
    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping("/")
    public List<Line> getLines(int start, int end) {
        return lineRepository.findAll();
    }

    @GetMapping("/{id}")
    public Line getLine(@PathVariable Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException("Line not found!"));
    }

    @PostMapping("/")
    public Line addLine(@Validated Line line, BindingResult result) {
        if (result.hasErrors()) {
            return null;
        }
        return lineRepository.save(line);
    }


}
