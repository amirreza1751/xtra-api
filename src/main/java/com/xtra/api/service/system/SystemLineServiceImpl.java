package com.xtra.api.service.system;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.admin.AdminLineMapper;
import com.xtra.api.model.Line;
import com.xtra.api.model.LineStatus;
import com.xtra.api.projection.admin.line.LineView;
import com.xtra.api.projection.system.LineAuth;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.service.LineService;
import com.xtra.api.service.admin.GeoIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SystemLineServiceImpl extends LineService {
    private final AdminLineMapper lineMapper;
    private final GeoIpService geoIpService;

    @Autowired
    public SystemLineServiceImpl(LineRepository repository, ConnectionRepository connectionRepository, AdminLineMapper lineMapper
            , BCryptPasswordEncoder bCryptPasswordEncoder, GeoIpService geoIpService) {
        super(repository, connectionRepository, bCryptPasswordEncoder);
        this.lineMapper = lineMapper;
        this.geoIpService = geoIpService;
    }

    private Line findByTokenOrFail(String token) {
        return repository.findByLineToken(token).orElseThrow(() -> new EntityNotFoundException(entityName, "token", token));
    }


    public LineStatus isLineEligibleForPlaying(LineAuth lineAuth) {
        var lineByToken = repository.findByLineToken(lineAuth.getLineToken());
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
        if (line.getBlockedUserAgents() != null && !line.getBlockedUserAgents().isEmpty()) {
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
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(lineMapper::convertToView);
    }

    public LineView getById(Long id) {
        return lineMapper.convertToView(findByIdOrFail(id));
    }


    public Long getLineIdByToken(String lineToken) {
        return findByTokenOrFail(lineToken).getId();
    }

    public Optional<Line> findById(Long lineId) {
        return repository.findById(lineId);
    }

}
