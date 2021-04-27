package com.xtra.api.service;

import com.xtra.api.model.Line;
import com.xtra.api.model.Connection;
import com.xtra.api.model.UserType;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.xtra.api.util.Utilities.generateRandomString;
import static com.xtra.api.util.Utilities.wrapSearchString;
import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class LineService extends CrudService<Line, Long, LineRepository> {
    private final ConnectionRepository connectionRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    protected LineService(LineRepository repository, ConnectionRepository connectionRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(repository, "Line");
        this.connectionRepository = connectionRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected Page<Line> findWithSearch(String search, Pageable page) {
        search = wrapSearchString(search);
        return repository.findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(search, search, search, page);
    }

    public void killAllConnections(Long id) {
        if (existsById(id)) {
            List<Connection> connections = connectionRepository.findAllByLineId(id);
            if (!connections.isEmpty()) {
                connections.forEach((activity) -> {
                    activity.setHlsEnded(true);
                    activity.setEndDate(LocalDateTime.now());
                    connectionRepository.save(activity);
                });
            }
        }
    }

    @Override
    public Line insert(Line line) {
        if (line.getRole().getType() != UserType.LINE) {
            throw new RuntimeException("role not suitable for line");
        }
        String lineUsername = line.getUsername();
        if (lineUsername == null || StringUtils.isEmpty(lineUsername)) {
            var isUnique = false;
            var username = "";
            while (!isUnique) {
                username = generateRandomString(8, 12, true);
                if (!repository.existsByUsername(username)) {
                    isUnique = true;
                }
            }
            line.setUsername(username);
        } else {
            if (repository.existsByUsername(lineUsername))
                //@todo change exception type
                throw new RuntimeException("line Username already exists");
        }
        line.setPassword(bCryptPasswordEncoder.encode(line.getPassword()));

        String token;
        do {
            token = generateRandomString(8, 12, false);
        }
        while (repository.findByToken(token).isPresent());
        line.setToken(token);
        return repository.save(line);
    }

    @Override
    public Line updateOrFail(Long id, Line newLine) {
        var line = findByIdOrFail(id);
        newLine.setPassword(bCryptPasswordEncoder.encode(newLine.getPassword()));
        copyProperties(newLine, line, "id", "username");
        return repository.save(line);
    }
}
