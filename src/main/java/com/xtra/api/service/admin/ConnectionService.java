package com.xtra.api.service.admin;

import com.maxmind.geoip2.model.CityResponse;
import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.ConnectionMapper;
import com.xtra.api.model.Connection;
import com.xtra.api.model.ConnectionId;
import com.xtra.api.projection.admin.ConnectionIdView;
import com.xtra.api.projection.admin.ConnectionView;
import com.xtra.api.projection.system.ConnectionDetails;
import com.xtra.api.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class ConnectionService {
    final private ConnectionRepository connectionRepository;
    final private AdminLineServiceImpl adminLineService;
    final private StreamService streamService;
    private final ServerService serverService;
    private final GeoIpService geoIpService;
    private final ConnectionMapper connectionMapper;

    @Autowired
    public ConnectionService(ConnectionRepository connectionRepository, AdminLineServiceImpl adminLineService, StreamService streamService, ServerService serverService, GeoIpService geoIpService, ConnectionMapper connectionMapper) {
        this.connectionRepository = connectionRepository;
        this.adminLineService = adminLineService;
        this.streamService = streamService;
        this.serverService = serverService;
        this.geoIpService = geoIpService;
        this.connectionMapper = connectionMapper;
    }

    public void deleteConnection(Long id) {
        connectionRepository.deleteById(id);
    }

    /*//    @Transactional
    public void batchCreateOrUpdate(List<Connection> connections, String token) {
        for (var connection : connections) {
            //@todo find out how null values get passed
//            var existingConnection = connectionRepository.findById(new ConnectionId(connection.getId().getLineId(), connection.getId().getStreamId(), serverService.findByServerToken(token).get().getId(), connection.getId().getUserIp()));
            var existingConnection = connectionRepository.findAll().get(0);
            if (existingConnection.isEmpty()) {
                var lineById = adminLineService.findById(connection.getLine().getId());
                if (lineById.isEmpty())
                    continue;
                connection.setLine(lineById.get());
                var streamById = streamService.findById(connection.getStream().getId());
//                if (streamById.isEmpty())
//                    continue;
//                connection.setStream((Stream) streamById.get());
                connection.setStream(streamById);

                var serverById = serverService.findByServerToken(token);
                if (serverById.isEmpty())
                    continue;
                connection.setServer(serverById.get());
                connection = this.setIpInformation(connection.getUserIp(), connection);
                connectionRepository.save(connection);

            } else {
                var oldActivity = existingConnection.get();
                copyProperties(connection, oldActivity, "id", "line", "stream", "server");
                var streamById = streamService.findById(connection.getStream().getId());
//                if (streamById.isEmpty())
//                    continue;
//                oldActivity.setStream((Stream) streamById.get());
                oldActivity.setStream(streamById);

                var lineById = adminLineService.findById(connection.getLine().getId());
                if (lineById.isEmpty())
                    continue;
                oldActivity.setLine(lineById.get());

                var serverById = serverService.findByServerToken(token);
                if (serverById.isEmpty())
                    continue;
                oldActivity.setServer(serverById.get());
                oldActivity = this.setIpInformation(connection.getUserIp(), oldActivity);
                connectionRepository.save(oldActivity);

            }

        }
    }
*/
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
            var connection = connectionRepository.findByLineIdAndServerIdAndStreamIdAndUserIp(line.getId(), server.getId(), stream.getId(), connectionDetails.getUserIp()).orElse(newConnection);
            connection.setStartDate(connectionDetails.getStartDate());
            connection.setEndDate(connectionDetails.getEndDate());
            connection.setLastRead(connectionDetails.getLastRead());
            connection.setHlsEnded(connectionDetails.isHlsEnded());
            connectionRepository.save(connection);
        }
    }

    @Transactional
    public void batchDelete(List<Connection> connectionDetailsList) {
        for (var activity : connectionDetailsList) {
            connectionRepository.deleteById(activity.getId());
        }
    }

    public Connection setIpInformation(String ip, Connection activity) {
        Optional<CityResponse> cityResponse = geoIpService.getIpInformation(ip);
        try {
            activity.setCountry(cityResponse.map(result -> result.getCountry().getName()).orElse("Unknown Country"));
            activity.setCity(cityResponse.map(result -> result.getCity().getName()).orElse("Unknown City"));
            activity.setIsoCode(cityResponse.map(result -> result.getCountry().getIsoCode()).orElse("Unknown ISO Code"));
            activity.setIsp(cityResponse.map(result -> result.getTraits().getIsp()).orElse("Unknown ISP"));
        } catch (NullPointerException ignored) {
        }
        return activity;
    }

    public Page<ConnectionView> getActiveConnections(int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(pageNo, pageSize, sortBy, sortDir).map(connectionMapper::convertToView);
    }

    public void endConnection(Long id) {
        var connection = connectionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        connection.setHlsEnded(true);
        connectionRepository.save(connection);
    }

    private Page<Connection> findAll(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable page;
        Sort.Order order;
        if (sortBy != null && !sortBy.equals("")) {
            if (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                order = Sort.Order.desc(sortBy);
            else
                order = Sort.Order.asc(sortBy);
            page = PageRequest.of(pageNo, pageSize, Sort.by(order));
        } else {
            page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc("id")));
        }
        return connectionRepository.findAll(page);
    }


}
