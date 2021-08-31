package com.xtra.api.repository.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogFilter {
    private String lineUsername;
    private String streamName;
    private String serverName;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private String search;
}
