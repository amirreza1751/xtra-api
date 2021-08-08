package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.LogMapper;
import com.xtra.api.model.line.*;
import com.xtra.api.model.stream.StreamProtocol;
import com.xtra.api.model.user.*;
import com.xtra.api.projection.admin.log.ActivityLogView;
import com.xtra.api.projection.admin.log.LoginLogView;
import com.xtra.api.projection.admin.log.ResellerLogView;
import com.xtra.api.repository.ActivityLogRepository;
import com.xtra.api.repository.LoginLogRepository;
import com.xtra.api.repository.ResellerLogRepository;
import com.xtra.api.repository.filter.*;
import com.xtra.api.util.OptionalBooleanBuilder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class LogService {
    private final LogMapper logMapper;
    private final GeoIpService geoIpService;
    private final ActivityLogRepository activityLogRepository;
    private final LoginLogRepository loginLogRepository;
    private final ResellerLogRepository resellerLogRepository;

    private final QActivityLog activityLog = QActivityLog.activityLog;
    private final QLoginLog loginLog = QLoginLog.loginLog;
    private final QResellerLog resellerLog = QResellerLog.resellerLog;

    public LogService(LogMapper logMapper, GeoIpService geoIpService, ActivityLogRepository activityLogRepository, LoginLogRepository loginLogRepository, ResellerLogRepository resellerLogRepository) {
        this.logMapper = logMapper;
        this.geoIpService = geoIpService;
        this.activityLogRepository = activityLogRepository;
        this.loginLogRepository = loginLogRepository;
        this.resellerLogRepository = resellerLogRepository;
    }

    public Page<ActivityLogView> getActivityLogs(int pageNo, int pageSize, String sortBy, String sortDir, ActivityLogFilter filter) {
        var builder = new ActivityLogFilterBuilder();
        var predicate = builder.build(filter);
        return activityLogRepository.findAll(predicate, getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(logMapper::convertToActivityLogView);
    }

    public ByteArrayResource downloadActivityLogsAsCsv(LocalDateTime dateFrom, LocalDateTime dateTo) {
        var predicate = new OptionalBooleanBuilder(activityLog.isNotNull())
                .notNullAnd(activityLog.start::after, dateFrom)
                .notNullAnd(activityLog.stop::before, dateTo)
                .build();
        var logs = activityLogRepository.findAll(predicate);
        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader("Line", "Stream", "Server", "Ip", "Player", "Country", "Start", "Stop", "Duration", "Output"))) {
            for (var log : logs) {
                printer.printRecord(log.getLine().getUsername(), log.getStream().getName(),
                        log.getServer().getName(), log.getIp(), log.getPlayer(), log.getCountry(),
                        log.getStart(), log.getStop(), log.getDuration(), log.getOutput());
            }
        } catch (IOException e) {
            log.error("error in writing file");
        }
        return new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8));
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

    public Page<LoginLogView> getLoginLogs(int pageNo, int pageSize, String sortBy, String sortDir, LoginLogFilter filter) {
        var builder = new LoginLogFilterBuilder();
        var predicate = builder.build(filter);
        return loginLogRepository.findAll(predicate, getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(logMapper::convertToLoginLogView);
    }

    public void saveLoginLog(LoginLog loginLog) {
        loginLogRepository.save(loginLog);
    }

    public ByteArrayResource downloadLoginLogsAsCsv(LocalDateTime dateFrom, LocalDateTime dateTo) {
        var predicate = new OptionalBooleanBuilder(loginLog.isNotNull())
                .notNullAnd(loginLog.date::after, dateFrom)
                .notNullAnd(loginLog.date::before, dateTo)
                .build();
        var logs = loginLogRepository.findAll(predicate);
        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader("ID", "User", "Type", "Ip", "Status", "Date"))) {
            for (var log : logs) {
                printer.printRecord(log.getId(), log.getUser().getUsername(), log.getType(),
                        log.getIp(), log.getStatus(), log.getDate());
            }
        } catch (IOException e) {
            log.error("error in writing file");
        }
        return new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    public Page<ResellerLogView> getResellerLogs(int pageNo, int pageSize, String sortBy, String sortDir, ResellerLogFilter filter) {
        var builder = new ResellerLogFilterBuilder();
        var predicate = builder.build(filter);
        return resellerLogRepository.findAll(predicate, getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(logMapper::convertToResellerLogView);
    }

    public void saveResellerLog(Reseller reseller, User user, LocalDateTime date, ResellerLogAction action) {
        resellerLogRepository.save(new ResellerLog(reseller.getUsername(), user.getUsername(), user.getUserType(), date, action));
    }

    public ByteArrayResource downloadResellerLogsAsCsv(LocalDateTime dateFrom, LocalDateTime dateTo) {
        var predicate = new OptionalBooleanBuilder(resellerLog.isNotNull())
                .notNullAnd(resellerLog.date::after, dateFrom)
                .notNullAnd(resellerLog.date::before, dateTo)
                .build();
        var logs = resellerLogRepository.findAll(predicate);
        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader("ID", "Reseller", "User/Subreseller", "Type", "Action", "Date"))) {
            for (var log : logs) {
                printer.printRecord(log.getId(), log.getResellerUsername(), log.getTargetUsername(), log.getTargetType(),
                        log.getAction(), log.getDate());
            }
        } catch (IOException e) {
            log.error("error in writing file");
        }
        return new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8));
    }

    public void clearResellerLogs(LocalDateTime dateFrom, LocalDateTime dateTo) {
        var predicate = new OptionalBooleanBuilder(resellerLog.isNotNull())
                .notNullAnd(resellerLog.date::after, dateFrom)
                .notNullAnd(resellerLog.date::before, dateTo)
                .build();
        var logs = resellerLogRepository.findAll(predicate);
        resellerLogRepository.deleteAll(logs);
    }

}
