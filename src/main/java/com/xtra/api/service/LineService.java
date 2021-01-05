package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.LineMapper;
import com.xtra.api.model.Line;
import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineStatus;
import com.xtra.api.projection.line.LineAuth;
import com.xtra.api.projection.line.LineInsertView;
import com.xtra.api.projection.line.LineView;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.LineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.xtra.api.util.Utilities.*;

@Service
public class LineService extends CrudService<Line, Long, LineRepository> {
    private final LineRepository lineRepository;
    private final LineActivityRepository lineActivityRepository;
    private final LineMapper lineMapper;
    private final GeoIpService geoIpService;

    public LineService(LineRepository lineRepository, LineActivityRepository lineActivityRepository, LineMapper lineMapper, GeoIpService geoIpService) {
        super(lineRepository, Line.class);
        this.lineRepository = lineRepository;
        this.lineActivityRepository = lineActivityRepository;
        this.lineMapper = lineMapper;
        this.geoIpService = geoIpService;
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


    public LineStatus isLineEligibleForPlaying(LineAuth lineAuth) {
        var lineByToken = lineRepository.findByLineToken(lineAuth.getLineToken());
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
            } else if (!isIpAllowed(line, lineAuth.getIpAddress())) {
                return LineStatus.FORBIDDEN;
            } else if (!isUserAgentAllowed(line, lineAuth.getUserAgent())) {
                return LineStatus.FORBIDDEN;
            } else if (false) {//@todo check access to stream
                return LineStatus.NO_ACCESS_TO_STREAM;
            } else {
                return LineStatus.OK;
            }
        } else
            return LineStatus.NOT_FOUND;
    }

    private boolean isUserAgentAllowed(Line line, String userAgent) {
        if (line.getAllowedUserAgents() != null && !line.getAllowedUserAgents().isEmpty()) {
            if (!line.getAllowedUserAgents().contains(userAgent))
                return false;
        }
        if (line.getBlockedUserAgents() != null && !line.getBlockedUserAgents().isEmpty()){
            return !line.getBlockedUserAgents().contains(userAgent);
        }
        return true;
    }

    private boolean isIpAllowed(Line line, String ipAddress) {
        if (line.isCountryLocked()) {
            var cityResponse = geoIpService.getIpInformation(ipAddress);
            if (cityResponse.isPresent()) {
                if (!line.getForcedCountry().getAlpha2().equals(cityResponse.get().getCountry().getIsoCode()))
                    return false;
            }
        }
        if (line.getBlockedIps() != null && !line.getBlockedIps().isEmpty()) {
            if (line.getBlockedIps().contains(ipAddress))
                return false;
        }
        if (line.getAllowedIps() != null && !line.getAllowedIps().isEmpty()) {
            return line.getAllowedIps().contains(ipAddress);
        }
        return true;
    }

    public Page<LineView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(lineMapper::convertToView).collect(Collectors.toList()));
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findByIdOrFail(id));
    }

    public LineView add(LineInsertView lineInsertView) {
        return lineMapper.convertToView(insert(lineMapper.convertToEntity(lineInsertView)));
    }

    @Override
    public Line insert(Line line) {
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

    public LineView save(Long id, LineInsertView lineInsertView) {
        return lineMapper.convertToView(updateOrFail(id, lineMapper.convertToEntity(lineInsertView)));
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
