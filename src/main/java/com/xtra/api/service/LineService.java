package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.Line;
import com.xtra.api.model.LineActivity;
import com.xtra.api.model.Reseller;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.xtra.api.util.Utilities.generateRandomString;

public abstract class LineService extends CrudService<Line, Long, LineRepository> {
    private final LineActivityRepository lineActivityRepository;

    protected LineService(LineRepository repository, Class<Line> aClass, LineActivityRepository lineActivityRepository) {
        super(repository, aClass);
        this.lineActivityRepository = lineActivityRepository;
    }

    protected boolean lineExists(Long id) {
        //@todo redundant code?
        if (repository.existsLineById(id))
            return true;
        else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }

    public void killAllConnections(Long id) {
        if (lineExists(id)) {
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
        if (StringUtils.isEmpty(line.getUsername())) {
            var isUnique = false;
            var username = "";
            while (!isUnique) {
                username = generateRandomString(5, 10, true);
                if (repository.findByUsername(username).isEmpty()) {
                    isUnique = true;
                }
            }
            line.setUsername(username);
        } else {
            if (repository.findByUsername(line.getUsername()).isPresent())
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
        while (repository.findByLineToken(token).isPresent());

        line.setLineToken(token);
        return repository.save(line);
    }

    public Line findByUsernameOrFail(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(aClass.getSimpleName(), "Username", username));
    }
}
