package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.Line;
import com.xtra.api.model.LineStatus;
import com.xtra.api.repository.LineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.xtra.api.util.Utilities.*;

@Service
public class LineService extends CrudService<Line, Long, LineRepository> {
    private final LineRepository lineRepository;
    private final ServerService serverService;

    public LineService(LineRepository lineRepository, ServerService serverService) {
        super(lineRepository, Line.class);
        this.lineRepository = lineRepository;
        this.serverService = serverService;
    }

    private Line findByTokenOrFail(String token) {
        var lineById = lineRepository.findByLineToken(token);
        if (lineById.isEmpty()) {
            throw new EntityNotFoundException(aClass.getSimpleName(), token);
        }
        return lineById.get();
    }

    private boolean lineExists(Long id) {
        if (lineRepository.existsLineById(id))
            return true;
        else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }


    public LineStatus isLineEligibleForPlaying(String lineToken, String stringToken) {
        var lineByToken = lineRepository.findByLineToken(lineToken);
        if (lineByToken.isPresent()) {
            var line = lineByToken.get();
            if (line.isBanned()) {
                return LineStatus.BANNED;
            } else if (line.isBlocked()) {
                return LineStatus.BLOCKED;
            } else if (!line.isNeverExpire() && line.getExpireDate().isBefore(LocalDateTime.now())) {
                return LineStatus.EXPIRED;
            } else if (line.getMaxConnections() == 0 || line.getMaxConnections() < line.getCurrentConnections()) {
                return LineStatus.MAX_CONNECTION_REACHED;
            } else if (false) {//@todo check access to stream
                return LineStatus.NO_ACCESS_TO_STREAM;
            } else {
                return LineStatus.OK;
            }
        } else
            return LineStatus.NOT_FOUND;
    }

    @Override
    public Line add(Line line) {
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

    public void updateLineBlock(Long id, boolean blocked) {
        Line line = findByIdOrFail(id);
        line.setBlocked(blocked);
        killAllConnections(id);
        lineRepository.save(line);
    }

    public void updateLineBan(Long id, boolean banned) {
        Line line = findByIdOrFail(id);
        line.setBanned(banned);
        killAllConnections(id);
        lineRepository.save(line);
    }

    public void killAllConnections(Long id) {
        if (lineExists(id)) {
            serverService.sendKillAllConnectionRequest(id);
        }
    }


    public Long getLineIdByToken(String lineToken) {
        return findByTokenOrFail(lineToken).getId();
    }

    public Optional<Line> findById(Long lineId) {
        return lineRepository.findById(lineId);
    }

    @Override
    protected Page<Line> findWithSearch(Pageable page, String search) {
        return lineRepository.findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(search, search, search, page);
    }
}