package com.xtra.api.projection.admin.analytics;

import java.time.LocalDateTime;

public interface ExpiringLines {
    String getUsername();
    LocalDateTime getExpireDate();
}
