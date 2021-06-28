package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.LogMapper;
import com.xtra.api.model.line.Connection;
import com.xtra.api.model.stream.StreamProtocol;
import com.xtra.api.projection.admin.log.ActivityLogView;
import com.xtra.api.repository.ActivityLogRepository;
import com.xtra.api.repository.filter.ActivityLogFilter;
import com.xtra.api.repository.filter.ActivityLogFilterBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService {
    private final LogMapper logMapper;
    private final GeoIpService geoIpService;
    private final ActivityLogRepository activityLogRepository;

    public LogService(LogMapper logMapper, GeoIpService geoIpService, ActivityLogRepository activityLogRepository) {
        this.logMapper = logMapper;
        this.geoIpService = geoIpService;
        this.activityLogRepository = activityLogRepository;
    }

    public Page<ActivityLogView> getActivityLogs(String search, int pageNo, int pageSize, String sortBy, String sortDir, ActivityLogFilter filter) {
        var builder = new ActivityLogFilterBuilder();
        var predicate = builder.build(filter);
        return activityLogRepository.findAll(predicate, getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(logMapper::convertToActivityLogView);
    }

    public void saveLogForConnections(List<Connection> connections) {
        var activityLogs = connections.stream().map(connection -> {
            var log = logMapper.convertConnectionToActivityLog(connection);
            log.setOutput(StreamProtocol.HLS);
            log.setDuration(Duration.between(log.getStart(), log.getStop()));
            geoIpService.getIpInformation(log.getIp()).ifPresent(cityResponse -> log.setCountry(cityResponse.getCountry().getName()));
            return log;
        }).collect(Collectors.toList());
        activityLogRepository.saveAll(activityLogs);
    }

    protected Pageable getSortingPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
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
        return page;
    }
}
