package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.Line;
import com.xtra.api.repository.LineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static com.xtra.api.util.Utilities.*;

@RestController
@RequestMapping("/lines")
public class LineController {
    LineRepository lineRepository;

    @Autowired
    public LineController(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @GetMapping("")
    public Page<Line> getLines(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);

        if (search == null)
            return lineRepository.findAll(page);
        else {
            search = wrapSearchString(search);
            return lineRepository.findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(search, search, search, page);
        }
    }

    @GetMapping("/{id}")
    public Line getLine(@PathVariable Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new RuntimeException("Line not found!"));
    }

    @PostMapping("/")
    public Line addLine(@RequestBody @Valid Line line) {
        if (StringUtils.isEmpty(line.getUsername())) {
            var isUnique = false;
            var username = "";
            while (!isUnique) {
                username = generateRandomString();
                if (lineRepository.findByUsername(username).isEmpty()) {
                    isUnique = true;
                }
            }
            line.setUsername(username);
        } else {
            if (lineRepository.findByUsername(line.getUsername()).isPresent())
                throw new RuntimeException("username already exists");
        }

        if (StringUtils.isEmpty(line.getPassword())) {
            var password = generateRandomString();
            line.setPassword(password);
        }
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

}
