package com.xtra.api.repository.filter;

import com.xtra.api.model.user.UserType;
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
    private String username;
    private UserType userType;
    private String ip;
    private String search;

}
