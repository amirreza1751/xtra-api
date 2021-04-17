package com.xtra.api.service.admin;

import com.maxmind.geoip2.model.CityResponse;
import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.admin.ConnectionMapper;
import com.xtra.api.model.Connection;
import com.xtra.api.model.ConnectionId;
import com.xtra.api.projection.admin.ConnectionIdView;
import com.xtra.api.projection.admin.ConnectionView;
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

    public void deleteConnection(ConnectionId activityId) {
        connectionRepository.deleteById(activityId);
    }

    //    @Transactional
    public void batchCreateOrUpdate(List<Connection> connections, String portNumber, HttpServletRequest request) {
        for (var activity : connections) {
            //@todo find out how null values get passed
            var existingActivity = connectionRepository.findById(new ConnectionId(activity.getId().getLineId(), activity.getId().getStreamId(), serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber).get().getId(), activity.getId().getUserIp()));
            if (existingActivity.isEmpty()) {
                var lineById = adminLineService.findById(activity.getId().getLineId());
                if (lineById.isEmpty())
                    continue;
                activity.setLine(lineById.get());
                var streamById = streamService.findById(activity.getId().getStreamId());
//                if (streamById.isEmpty())
//                    continue;
//                activity.setStream((Stream) streamById.get());
                activity.setStream(streamById);

                var serverById = serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber);
                if (serverById.isEmpty())
                    continue;
                activity.setServer(serverById.get());
                activity = this.setIpInformation(activity.getId().getUserIp(), activity);
                connectionRepository.save(activity);

            } else {
                var oldActivity = existingActivity.get();
                copyProperties(activity, oldActivity, "id", "line", "stream", "server");
                var streamById = streamService.findById(activity.getId().getStreamId());
//                if (streamById.isEmpty())
//                    continue;
//                oldActivity.setStream((Stream) streamById.get());
                oldActivity.setStream(streamById);

                var lineById = adminLineService.findById(activity.getId().getLineId());
                if (lineById.isEmpty())
                    continue;
                oldActivity.setLine(lineById.get());

                var serverById = serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber);
                if (serverById.isEmpty())
                    continue;
                oldActivity.setServer(serverById.get());
                oldActivity = this.setIpInformation(activity.getId().getUserIp(), oldActivity);
                connectionRepository.save(oldActivity);

            }

        }
    }

    @Transactional
    public void batchDelete(List<Connection> connections) {
        for (var activity : connections) {
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

    public void endConnection(ConnectionIdView connectionIdView) {
        ConnectionId connectionId = connectionMapper.convertToEntity(connectionIdView);
        var connection = connectionRepository.findById(connectionId).orElseThrow(EntityNotFoundException::new);
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
