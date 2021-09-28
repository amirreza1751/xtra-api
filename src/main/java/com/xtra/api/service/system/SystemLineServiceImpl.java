package com.xtra.api.service.system;

import com.maxmind.geoip2.model.CityResponse;
import com.xtra.api.mapper.admin.AdminLineMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.line.Connection;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.line.LineStatus;
import com.xtra.api.model.line.VodConnection;
import com.xtra.api.model.stream.Stream;
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
import java.util.stream.Collectors;

@Service
public class SystemLineServiceImpl extends LineService {
    private final AdminLineMapper lineMapper;
    private final GeoIpService geoIpService;
    private final StreamRepository streamRepository;
    private final ServerRepository serverRepository;
    private final BlockedIpRepository blockedIpRepository;
    private final VideoRepository videoRepository;

    @Autowired
    public SystemLineServiceImpl(LineRepository repository, ConnectionRepository connectionRepository, AdminLineMapper lineMapper
            , BCryptPasswordEncoder bCryptPasswordEncoder, GeoIpService geoIpService, RoleRepository roleRepository
            , StreamRepository streamRepository, ServerRepository serverRepository, BlockedIpRepository blockedIpRepository
            , VideoRepository videoRepository, UserRepository userRepository, VodConnectionRepository vodConnectionRepository) {

        super(repository, connectionRepository, bCryptPasswordEncoder, roleRepository, userRepository,vodConnectionRepository);
        this.lineMapper = lineMapper;
        this.geoIpService = geoIpService;
        this.streamRepository = streamRepository;
        this.serverRepository = serverRepository;
        this.blockedIpRepository = blockedIpRepository;
        this.videoRepository = videoRepository;
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

            var currentConnections = totalConnectionsCount(line.getId());
            var connection = connectionRepository.findByLineLineTokenAndServerTokenAndStream_StreamTokenAndUserIp(lineAuth.getLineToken(), lineAuth.getServerToken(), lineAuth.getMediaToken(), lineAuth.getIpAddress())
                    .orElse(new Connection(line, stream, server, lineAuth.getIpAddress()));
            if (connection.getId() == null) {
                connection.setStartDate(LocalDateTime.now());
                Optional<CityResponse> geoResponse = geoIpService.getIpInformation(connection.getUserIp());
                if (geoResponse.isPresent()) {
                    var geo = geoResponse.get();
                    connection.setCity(geo.getCity().getName());
                    connection.setCountry(geo.getCountry().getName());
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
            } else if (!isIpAllowed(line, lineAuth.getIpAddress()) || !isUserAgentAllowed(line, lineAuth.getUserAgent())) {
                return LineStatus.FORBIDDEN;
            } else if (!isCollectionAllowed(line, stream)) {
                return LineStatus.NO_ACCESS_TO_STREAM;
            } else {
                connection.setLastRead(LocalDateTime.now());
                connectionRepository.save(connection);
                return LineStatus.OK;
            }
        } else
            return LineStatus.NOT_FOUND;
    }


    public LineStatus canLinePlayVod(LineAuth lineAuth) {
        var lineByToken = repository.findByLineToken(lineAuth.getLineToken());
        var videoByToken = videoRepository.findByToken(lineAuth.getMediaToken());
        var serverByToken = serverRepository.findByToken(lineAuth.getServerToken());
        if (lineByToken.isPresent() && videoByToken.isPresent() && serverByToken.isPresent()) {
            var line = lineByToken.get();
            var video = videoByToken.get();
            var server = serverByToken.get();

            var currentConnections = totalConnectionsCount(line.getId());
            var vodConnection = vodConnectionRepository.findByLineLineTokenAndServerTokenAndVideo_tokenAndUserIp(lineAuth.getLineToken(), lineAuth.getServerToken(), lineAuth.getMediaToken(), lineAuth.getIpAddress())
                    .orElse(new VodConnection(line, video, server, lineAuth.getIpAddress()));
            if (vodConnection.getId() == null) {
                vodConnection.setStartDate(LocalDateTime.now());
                Optional<CityResponse> geoResponse = geoIpService.getIpInformation(vodConnection.getUserIp());
                if (geoResponse.isPresent()) {
                    var geo = geoResponse.get();
                    vodConnection.setCity(geo.getCity().getName());
                    vodConnection.setCountry(geo.getCountry().getName());
                    vodConnection.setIsoCode(geo.getCountry().getIsoCode());
                    vodConnection.setIsp(geo.getTraits().getIsp());
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
            } else if (!isIpAllowed(line, lineAuth.getIpAddress()) || !isUserAgentAllowed(line, lineAuth.getUserAgent())) {
                return LineStatus.FORBIDDEN;
            } else if (false) {//@todo check access to stream
                return LineStatus.NO_ACCESS_TO_STREAM;
            }
            else {
                vodConnection.setLastRead(LocalDateTime.now());
                vodConnectionRepository.save(vodConnection);
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
        var blocked = blockedIpRepository.findById(ipAddress);
        if (blocked.isPresent() && blocked.get().getUntil().isAfter(LocalDateTime.now())) {
            return false;
        }
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

    public boolean isCollectionAllowed(Line line, Stream stream){
        return line.getDefaultDownloadList().getCollectionsAssign().stream().map(downloadListCollection -> downloadListCollection.getCollection().getId()).anyMatch(stream.getCollectionAssigns().stream().map(collectionStream -> collectionStream.getCollection().getId()).collect(Collectors.toSet())::contains);
    }

}
