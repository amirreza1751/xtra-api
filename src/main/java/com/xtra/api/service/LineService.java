package com.xtra.api.service;

import com.xtra.api.model.Line;
import com.xtra.api.model.LineActivity;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.xtra.api.util.Utilities.generateRandomString;

public abstract class LineService extends CrudService<Line, Long, LineRepository> {
    private final LineActivityRepository lineActivityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    protected LineService(LineRepository repository, LineActivityRepository lineActivityRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(repository, "Line");
        this.lineActivityRepository = lineActivityRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public void killAllConnections(Long id) {
        if (existsById(id)) {
            List<LineActivity> lineActivities = lineActivityRepository.findAllByIdLineId(id);
            if (!lineActivities.isEmpty()) {
                lineActivities.forEach((activity) -> {
                    activity.setHlsEnded(true);
                    activity.setEndDate(LocalDateTime.now());
                    lineActivityRepository.save(activity);
                });
            }
        }
    }

    @Override
    public Line insert(Line line) {
        String lineUsername = line.getUsername();
        if (lineUsername == null || StringUtils.isEmpty(lineUsername)) {
            var isUnique = false;
            var username = "";
            while (!isUnique) {
                username = generateRandomString(8, 12, true);
                if (!repository.exitsByUsername(username)) {
                    isUnique = true;
                }
            }
            line.setUsername(username);
        } else {
            if (repository.exitsByUsername(lineUsername))
                throw new RuntimeException("lineUsername already exists");
        }
        line.setPassword(bCryptPasswordEncoder.encode(line.getPassword()));

        /*String token;
        do {
            token = generateRandomString(8, 12, false);
        }
        while (repository.findByLineToken(token).isPresent());
        line.setLineToken(token);*/
        return repository.save(line);
    }

    public Line getCurrentLine() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            var principal = auth.getPrincipal();
            return repository.findByUsername(((User) principal).getUsername()).orElseThrow(() -> new AccessDeniedException("access denied"));
        }
        throw new AccessDeniedException("access denied");
    }
}
