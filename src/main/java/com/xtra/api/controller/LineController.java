package com.xtra.api.controller;

import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.Line;
import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineStatus;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.xtra.api.util.Utilities.*;
import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/lines")
public class LineController {
    LineRepository lineRepository;
    StreamRepository streamRepository;
    LineService lineService;
    LineActivityRepository lineActivityRepository;
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    public LineController(LineRepository lineRepository, LineService lineService, LineActivityRepository lineActivityRepository, StreamRepository streamRepository) {
        this.lineRepository = lineRepository;
        this.lineService = lineService;
        this.lineActivityRepository = lineActivityRepository;
        this.streamRepository = streamRepository;
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

    @PostMapping("")
    public Line addLine(@RequestBody @Valid Line line) {
        if (StringUtils.isEmpty(line.getUsername())) {
            var isUnique = false;
            var username = "";
            while (!isUnique) {
                username = generateRandomString(5, 10, true);
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
            var password = generateRandomString(5, 10, true);
            line.setPassword(password);
        }

        String token;
        do {
            token = generateRandomString(8, 12, false);
        }
        while (lineRepository.findByLineToken(token).isPresent());

        line.setLineToken(token);
        return lineRepository.save(line);
    }

    @PatchMapping("/{id}")
    public Line updateLine(@PathVariable Long id, @RequestBody @Valid Line line) {
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

    @GetMapping("/ban/{id}")
    public void banLine(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean banned) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isEmpty()) {
            throw new EntityNotFound();
        }
        Line l = line.get();
        l.setBanned(banned);
        lineRepository.save(l);
    }

    @GetMapping("/kill_connections/{id}")
    public ResponseEntity<String> killLineConnections(@PathVariable Long id) {
        if (lineRepository.findById(id).isPresent()) {
            var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/lines/kill_connections/" + id, Boolean.class);
        }
        return ResponseEntity.ok().body("");
    }

    @GetMapping("/stream_auth/{line_token}/{stream_token}")
    public LineStatus authorizeLine(@PathVariable String line_token, @PathVariable String stream_token) {
        return lineService.isLineEligibleForPlaying(line_token, stream_token);
    }

    @GetMapping("/get_id/{line_token}")
    public Long getLineByToken(@PathVariable("line_token") String lineToken) {
        var lineByToken = lineRepository.findByLineToken(lineToken);
        return lineByToken.map(Line::getId).orElse(null);
    }

    @PostMapping("/updateLineActivities")
    @Transactional
    public void updateLineActivities(@RequestBody List<LineActivity> lineActivities) {
        for (var activity : lineActivities) {
            var existingActivity = lineActivityRepository.findByLineIdAndUserIp(activity.getLineId(), activity.getUserIp());
            if (existingActivity.isEmpty()) {
                //@todo find out how null values get passed
                if (activity.getLineId() == null || activity.getStreamId() == null)
                    continue;

                var lineById = lineRepository.findById(activity.getLineId());
                if (lineById.isEmpty())
                    continue;
                activity.setLine(lineById.get());
                var streamById = streamRepository.findById(activity.getStreamId());
                if (streamById.isEmpty())
                    continue;
                activity.setStream(streamById.get());
                lineActivityRepository.save(activity);
            } else {
                var oldActivity = existingActivity.get();
                copyProperties(oldActivity, activity, "id", "user", "stream");
                lineActivityRepository.save(oldActivity);
            }

        }
    }

    @PostMapping("/deleteLineActivities")
    @Transactional
    public void deleteLineActivities(@RequestBody List<LineActivity> lineActivities) {
        for (var activity : lineActivities) {
            var existingActivity = lineActivityRepository.findByLineIdAndUserIp(activity.getLineId(), activity.getUserIp());
            existingActivity.ifPresent(lineActivity -> lineActivityRepository.delete(lineActivity));
        }
    }
}
