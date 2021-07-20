package com.xtra.api.repository.filter;

import com.xtra.api.model.user.ResellerLogAction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResellerLogFilter {
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private Long resellerId;
    private ResellerLogAction action;
    private String search;

}
