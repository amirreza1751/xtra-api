package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.log.ActivityLogView;
import com.xtra.api.projection.admin.log.LoginLogView;
import com.xtra.api.repository.filter.ActivityLogFilter;
import com.xtra.api.repository.filter.LoginLogFilter;
import com.xtra.api.service.admin.LogService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequestMapping("logs")
@RestController
public class LogController {
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/activity-logs")
    public Page<ActivityLogView> getActivityLogs(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir
            , @RequestParam(required = false, name = "server_id") Long serverId, @RequestParam(required = false, name = "stream_id") Long streamId
            , @RequestParam(required = false, name = "line_id") Long lineId, @RequestParam(required = false, name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom
            , @RequestParam(required = false, name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return logService.getActivityLogs(pageNo, pageSize, sortBy, sortDir, new ActivityLogFilter(lineId, streamId, serverId, dateFrom, dateTo, search));
    }

    @GetMapping("/activity-logs/export")
    public ResponseEntity<?> downloadActivityLogsAsCsv(@RequestParam(name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
                                                       @RequestParam(name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        var resource = logService.downloadActivityLogsAsCsv(dateFrom, dateTo);
        return ResponseEntity.ok().contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"activity_log_export-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ".csv\"")
                .body(resource);
    }

    @GetMapping("/login-logs")
    public Page<LoginLogView> getLoginLogs(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir
            , @RequestParam(required = false, name = "user_id") Long userId, @RequestParam(required = false, name = "ip") String ip
            , @RequestParam(required = false, name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom
            , @RequestParam(required = false, name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return logService.getLoginLogs(pageNo, pageSize, sortBy, sortDir, new LoginLogFilter(dateFrom, dateTo, userId, search));

    }

    @GetMapping("/login-logs/export")
    public ResponseEntity<?> downloadLoginLogsAsCsv(@RequestParam(name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
                                                       @RequestParam(name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        var resource = logService.downloadLoginLogsAsCsv(dateFrom, dateTo);
        return ResponseEntity.ok().contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"login_log_export-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ".csv\"")
                .body(resource);
    }

}
