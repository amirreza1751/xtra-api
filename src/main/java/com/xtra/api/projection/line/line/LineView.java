package com.xtra.api.projection.line.line;

import com.xtra.api.projection.admin.BaseView;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LineView implements BaseView {
    private Long id;
    private String username;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isBanned = false;
}
