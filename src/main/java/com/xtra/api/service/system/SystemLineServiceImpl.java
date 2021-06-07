package com.xtra.api.service.system;

import com.maxmind.geoip2.model.CityResponse;
import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.AdminLineMapper;
import com.xtra.api.model.Connection;
import com.xtra.api.model.Line;
import com.xtra.api.model.LineStatus;
import com.xtra.api.model.Stream;
import com.xtra.api.projection.admin.line.LineView;
import com.xtra.api.projection.system.LineAuth;
import com.xtra.api.repository.*;
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
    private final StreamRepository<Stream> streamRepository;
    private final ServerRepository serverRepository;

    @Autowired
    public SystemLineServiceImpl(LineRepository repository, ConnectionRepository connectionRepository, AdminLineMapper lineMapper
            , BCryptPasswordEncoder bCryptPasswordEncoder, GeoIpService geoIpService, RoleRepository roleRepository, StreamRepository streamRepository, ServerRepository serverRepository) {
        super(repository, connectionRepository, bCryptPasswordEncoder, roleRepository);
        this.lineMapper = lineMapper;
        this.geoIpService = geoIpService;
        this.streamRepository = streamRepository;
        this.serverRepository = serverRepository;
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

    private Line findByTokenOrFail(String token) {
        return repository.findByLineToken(token).orElseThrow(() -> new EntityNotFoundException(entityName, "token", token));
    }


    public LineStatus canLinePlayStream(LineAuth lineAuth) {
        var lineByToken = repository.findByLineToken(lineAuth.getLineToken());
        var streamByToken = streamRepository.findByStreamToken(lineAuth.getMediaToken());
        var serverByToken = serverRepository.findByToken(lineAuth.getServerToken());

        if (lineByToken.isPresent() && streamByToken.isPresent() && serverByToken.isPresent()) {
            var line = lineByToken.get();
            var stream = streamByToken.get();
            var server = serverByToken.get();

            var currentConnections = getConnectionsCount(line.getId());
            var connection = connectionRepository.findByLineLineTokenAndServerTokenAndStream_StreamTokenAndUserIp(lineAuth.getLineToken(), lineAuth.getServerToken(), lineAuth.getMediaToken(), lineAuth.getIpAddress())
                    .orElse(new Connection(line, stream, server, lineAuth.getIpAddress()));
            if (connection.getId() == null) {
                connection.setStartDate(LocalDateTime.now());
                Optional<CityResponse> geoResponse = geoIpService.getIpInformation(connection.getUserIp());
                if (geoResponse.isPresent()) {
                    var geo = geoResponse.get();
                    connection.setCity(geo.getCity().toString());
                    connection.setCountry(geo.getCountry().toString());
                    connection.setIsoCode(geo.getCountry().getIsoCode());
                    connection.setIsp(geo.getTraits().getIsp());
                }
            }

            if (line.isBanned()) {
                return LineStatus.BANNED;
            } else if (line.isBlocked()) {
                return LineStatus.BLOCKED;
            } else if (!line.isNeverExpire() && line.getExpireDate().isBefore(LocalDateTime.now())) {
                return LineStatus.EXPIRED;
            } else if (line.getMaxConnections() == 0 || line.getMaxConnections() < currentConnections) {
                return LineStatus.MAX_CONNECTION_REACHED;
            } else if (!isIpAllowed(line, lineAuth.getIpAddress()) || !isUserAgentAllowed(line, lineAuth.getUserAgent()) || connection.isKilled()) {
                return LineStatus.FORBIDDEN;
            } else if (false) {//@todo check access to stream
                return LineStatus.NO_ACCESS_TO_STREAM;
            } else {
                connection.setLastRead(LocalDateTime.now());
                connectionRepository.save(connection);
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


}
