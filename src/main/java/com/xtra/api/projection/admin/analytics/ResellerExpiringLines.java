package com.xtra.api.projection.admin.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResellerExpiringLines {
    private String username;
    private String expireDate;
}
