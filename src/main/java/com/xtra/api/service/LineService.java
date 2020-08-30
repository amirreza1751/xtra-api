package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.Line;
import com.xtra.api.model.LineStatus;
import com.xtra.api.repository.LineRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.xtra.api.util.Utilities.*;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final ServerService serverService;

    public LineService(LineRepository lineRepository, ServerService serverService) {
        this.lineRepository = lineRepository;
        this.serverService = serverService;
    }

    private Line getLineIfExists(Long id) {
        var lineById = lineRepository.findById(id);
        if (lineById.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return lineById.get();
    }

    private Line getLineIfExists(String token) {
        var lineById = lineRepository.findByLineToken(token);
        if (lineById.isEmpty()) {
            throw new EntityNotFoundException();
        }
        return lineById.get();
    }

    private boolean lineExists(Long id) {
        if (lineRepository.existsLineById(id))
            return true;
        else
            throw new EntityNotFoundException();
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

    public Page<Line> getLines(int pageNo, int pageSize, String search, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);

        if (search == null)
            return lineRepository.findAll(page);
        else {
            search = wrapSearchString(search);
            return lineRepository.findByUsernameLikeOrAdminNotesLikeOrResellerNotesLike(search, search, search, page);
        }
    }

    public Line addLine(Line line) {
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

    public Line updateLine(Long id, Line line) {
        if (!lineExists(id))
            throw new EntityNotFoundException();
        line.setId(id);
        //@todo partial update
        return lineRepository.save(line);
    }

    public Line getLine(Long id) {
        return getLineIfExists(id);
    }

    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    public void blockLine(Long id) {
        Line line = getLineIfExists(id);
        line.setBlocked(true);
        killAllConnections(id);
        lineRepository.save(line);
    }

    public void unblockLine(Long id) {
        Line line = getLineIfExists(id);
        line.setBlocked(false);
        lineRepository.save(line);
    }

    public void banLine(Long id) {
        Line line = getLineIfExists(id);
        line.setBanned(true);
        killAllConnections(id);
        lineRepository.save(line);
    }

    public void unbanLine(Long id) {
        Line line = getLineIfExists(id);
        line.setBanned(false);
        lineRepository.save(line);
    }

    public void killAllConnections(Long id) {
        if (lineExists(id)) {
            serverService.sendKillAllConnectionRequest(id);
        }
    }


    public Long getLineIdByToken(String lineToken) {
        return getLineIfExists(lineToken).getId();
    }

    public Optional<Line> findById(Long lineId) {
        return lineRepository.findById(lineId);
    }
}
