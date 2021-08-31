package com.xtra.api.repository.filter;

import com.xtra.api.model.user.CreditLogReason;
import com.xtra.api.model.user.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditLogFilter {
    private String actorUsername;
    private UserType actorUserType;
    private String targetUsername;
    private Integer changeAmountLTE;
    private Integer changeAmountGTE;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private CreditLogReason reason;
    private String search;
}
