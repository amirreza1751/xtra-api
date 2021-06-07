package com.xtra.api.service.admin;

import com.maxmind.geoip2.model.CityResponse;
import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.ConnectionMapper;
import com.xtra.api.model.Connection;
import com.xtra.api.projection.admin.ConnectionView;
import com.xtra.api.projection.system.ConnectionDetails;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionService extends CrudService<Connection, Long, ConnectionRepository> {
    final private AdminLineServiceImpl adminLineService;
    final private StreamService streamService;
    private final ServerService serverService;
    private final GeoIpService geoIpService;
    private final ConnectionMapper connectionMapper;

    @Autowired
    public ConnectionService(ConnectionRepository repository, AdminLineServiceImpl adminLineService, StreamService streamService, ServerService serverService, GeoIpService geoIpService, ConnectionMapper connectionMapper) {
        super(repository, "Connection");
        this.adminLineService = adminLineService;
        this.streamService = streamService;
        this.serverService = serverService;
        this.geoIpService = geoIpService;
        this.connectionMapper = connectionMapper;
    }

    public Page<ConnectionView> getActiveConnections(int pageNo, int pageSize, String sortBy, String sortDir) {
        return repository.findAllByKilledFalse(getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(connectionMapper::convertToView);
    }

    public void updateConnections(String token, List<ConnectionDetails> connectionDetailsList) {
        var serverByToken = serverService.findByServerToken(token);
        if (serverByToken.isEmpty())
            return;
        var server = serverByToken.get();
        for (var connectionDetails : connectionDetailsList) {
            if (connectionDetails.getLineToken() == null || connectionDetails.getStreamToken() == null || connectionDetails.getUserIp() == null)
                continue;
            var line = adminLineService.findByTokenOrFail(connectionDetails.getLineToken());
            var stream = streamService.findByTokenOrFail(connectionDetails.getStreamToken());
            if (stream == null)
                continue;
            var newConnection = new Connection(line, stream, server, connectionDetails.getUserIp());
            var connection = repository.findByLineIdAndServerIdAndStreamIdAndUserIp(line.getId(), server.getId(), stream.getId(), connectionDetails.getUserIp()).orElse(newConnection);
            connection.setStartDate(connectionDetails.getStartDate());
            connection.setEndDate(connectionDetails.getEndDate());
            connection.setLastRead(connectionDetails.getLastRead());
            connection.setKilled(connectionDetails.isHlsEnded());
            repository.save(connection);
        }
    }

    public void endConnection(Long id) {
        var connection = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        connection.setKilled(true);
        repository.save(connection);
    }

    @Transactional
    public void deleteOldConnections() {
        repository.deleteAllByKilledTrueAndEndDateBefore(LocalDateTime.now().minusMinutes(1));
        //delete connections not updated longer than one segment time
        repository.deleteAllByLastReadIsLessThanEqual(LocalDateTime.now().minusSeconds(10));
    }

    @Override
    protected Page<Connection> findWithSearch(String search, Pageable page) {
        return null;
    }
}
