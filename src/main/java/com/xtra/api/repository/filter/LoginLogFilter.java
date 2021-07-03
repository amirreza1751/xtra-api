package com.xtra.api.repository.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginLogFilter {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Long userId;
    private String search;

}
