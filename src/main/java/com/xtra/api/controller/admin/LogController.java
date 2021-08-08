package com.xtra.api.controller.admin;

import com.xtra.api.model.user.CreditLogReason;
import com.xtra.api.model.user.ResellerLogAction;
import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.log.ActivityLogView;
import com.xtra.api.projection.admin.log.CreditLogView;
import com.xtra.api.projection.admin.log.LoginLogView;
import com.xtra.api.projection.admin.log.ResellerLogView;
import com.xtra.api.repository.filter.ActivityLogFilter;
import com.xtra.api.repository.filter.CreditLogFilter;
import com.xtra.api.repository.filter.LoginLogFilter;
import com.xtra.api.repository.filter.ResellerLogFilter;
import com.xtra.api.service.CreditLogService;
import com.xtra.api.service.admin.LogService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    private final CreditLogService creditLogService;

    public LogController(LogService logService, CreditLogService creditLogService) {
        this.logService = logService;
        this.creditLogService = creditLogService;
    }

    @GetMapping("/activity-logs")
    public Page<ActivityLogView> getActivityLogs(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir
            , @RequestParam(required = false, name = "server_name") String serverName, @RequestParam(required = false, name = "stream_name") String streamName
            , @RequestParam(required = false, name = "line_username") String lineUsername, @RequestParam(required = false, name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom
            , @RequestParam(required = false, name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return logService.getActivityLogs(pageNo, pageSize, sortBy, sortDir, new ActivityLogFilter(lineUsername, streamName, serverName, dateFrom, dateTo, search));
    }

    @GetMapping("/activity-logs/export")
    public ResponseEntity<?> downloadActivityLogsAsCsv(
            @RequestParam(name = "date_from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(name = "date_to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        var resource = logService.downloadActivityLogsAsCsv(dateFrom, dateTo);
        return ResponseEntity.ok().contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"activity_log_export-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ".csv\"")
                .body(resource);
    }

    @GetMapping("/login-logs")
    public Page<LoginLogView> getLoginLogs(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
            @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir,
            @RequestParam(required = false, name = "username") String username,
            @RequestParam(required = false, name="usetType") UserType userType,
            @RequestParam(required = false, name = "ip") String ip,
            @RequestParam(required = false, name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false, name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return logService.getLoginLogs(pageNo, pageSize, sortBy, sortDir, new LoginLogFilter(dateFrom, dateTo, username, userType, ip, search));

    }

    @GetMapping("/login-logs/export")
    public ResponseEntity<?> downloadLoginLogsAsCsv(
            @RequestParam(name = "date_from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(name = "date_to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        var resource = logService.downloadLoginLogsAsCsv(dateFrom, dateTo);
        return ResponseEntity.ok().contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"login_log_export-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ".csv\"")
                .body(resource);
    }

    @GetMapping("/credit-logs")
    public Page<CreditLogView> getCreditLogs(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
            @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir,
            @RequestParam(required = false, name = "actor_usern") String actorUsername, @RequestParam(required = false, name = "actor_type") UserType actorUserType,
            @RequestParam(required = false, name = "target_username") String targetUsername, @RequestParam(required = false, name = "amount_gte") Integer changeAmountGTE,
            @RequestParam(required = false, name = "amount_lte") Integer changeAmountLTE, @RequestParam(required = false, name = "reason") CreditLogReason reason,
            @RequestParam(required = false, name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false, name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return creditLogService.getCreditLogs(pageNo, pageSize, sortBy, sortDir, new CreditLogFilter(actorUsername, actorUserType, targetUsername, changeAmountLTE, changeAmountGTE, dateFrom, dateTo, reason, search));
    }

    @GetMapping("/credit-logs/export")
    public ResponseEntity<?> downloadCreditLogsAsCsv(
            @RequestParam(name = "date_from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(name = "date_to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        var resource = creditLogService.downloadCreditLogsAsCsv(dateFrom, dateTo);
        return ResponseEntity.ok().contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"activity_log_export-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ".csv\"")
                .body(resource);
    }

    @GetMapping("/reseller-logs")
     public Page<ResellerLogView> getResellerLogs(
            @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
            @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir,
            @RequestParam(required = false, name = "reseller_username") String resellerUsername, @RequestParam(required = false, name = "action") ResellerLogAction action,
            @RequestParam(required = false, name = "date_from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false, name = "date_to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        return logService.getResellerLogs(pageNo, pageSize, sortBy, sortDir, new ResellerLogFilter(dateFrom, dateTo, resellerUsername, action, search));
    }

    @GetMapping("/reseller-logs/export")
    public ResponseEntity<?> downloadResellerLogsAsCsv(
            @RequestParam(name = "date_from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(name = "date_to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo) {
        var resource = logService.downloadResellerLogsAsCsv(dateFrom, dateTo);
        return ResponseEntity.ok().contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"reseller_log_export-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ".csv\"")
                .body(resource);
    }

    @GetMapping("/reseller-logs/clear")
    public ResponseEntity<?> clearResellerLogs() {
        logService.clearResellerLogs();
        return ResponseEntity.ok().build();
    }

}
