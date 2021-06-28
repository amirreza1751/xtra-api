package com.xtra.api.repository.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogFilter {
    private Long lineId;
    private Long streamId;
    private Long serverId;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

}
