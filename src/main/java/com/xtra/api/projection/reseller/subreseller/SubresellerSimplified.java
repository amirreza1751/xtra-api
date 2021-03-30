package com.xtra.api.projection.reseller.subreseller;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubresellerSimplified {
    private long id;
    private String username;
    private int credits;
    private LocalDateTime lastLoginDate;
    private String lastLoginIp;
}
