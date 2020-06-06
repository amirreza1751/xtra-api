package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.Line;
import com.xtra.api.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/lines")
public class LineController {
    LineRepository lineRepository;

    @Autowired
    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping("/")
    public Page<Line> getLines(@RequestParam(defaultValue = "0", name = "page_no") int page_no, @RequestParam(defaultValue = "25", name = "page_size") int page_size) {
        Pageable page = PageRequest.of(page_no, page_size);
        return lineRepository.findAll(page);
    }

    @GetMapping("/{id}")
    public Line getLine(@PathVariable Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException("Line not found!"));
    }

    @PostMapping("/")
    public Line addLine(@RequestBody Line line) {
        return lineRepository.save(line);
    }

    @PatchMapping("/{id}")
    public Line updateLine(@PathVariable Long id, @RequestBody Line line) {
        if (lineRepository.findById(id).isEmpty()) {
            throw new EntityNotFound();
        }
        line.setId(id);
        return lineRepository.save(line);
    }

    @DeleteMapping("/{id}")
    public void deleteLine(@PathVariable Long id) {
        lineRepository.deleteById(id);
    }

    @GetMapping("/block/{id}")
    public void blockLine(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean blocked) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isEmpty()) {
            throw new EntityNotFound();
        }
        Line l = line.get();
        l.setBlocked(blocked);
        lineRepository.save(l);
    }

    @GetMapping("/admin_block/{id}")
    public void adminBlockLine(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean blocked) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isEmpty()) {
            throw new EntityNotFound();
        }
        Line l = line.get();
        l.setAdminBlocked(blocked);
        lineRepository.save(l);
    }




}
