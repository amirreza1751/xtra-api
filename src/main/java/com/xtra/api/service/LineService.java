package com.xtra.api.service;

import com.xtra.api.model.LineStatus;
import com.xtra.api.repository.LineRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
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
            } else if (line.getMaxConnections() == 0 || line.getMaxConnections() == line.getCurrentConnections()) {
                return LineStatus.MAX_CONNECTION_REACHED;
            } else if (false) {//@todo check access to stream
                return LineStatus.NO_ACCESS_TO_STREAM;
            } else
                {
                return LineStatus.OK;
            }
        } else
            return LineStatus.NOT_FOUND;
    }

}
