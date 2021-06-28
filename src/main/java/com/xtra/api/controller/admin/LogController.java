package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.log.ActivityLogView;
import com.xtra.api.repository.filter.ActivityLogFilter;
import com.xtra.api.service.admin.LogService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
        return logService.getActivityLogs(search, pageNo, pageSize, sortBy, sortDir, new ActivityLogFilter(lineId, streamId, serverId, dateFrom, dateTo));
    }
}
