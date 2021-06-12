package com.xtra.api.projection.admin.line;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LineListView {
    private Long id;
    private String username;
    private String owner;
    private boolean isOnline;
    private boolean isTrial;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime expireDate;
    private boolean neverExpire;
    private int maxConnections;
    private int currentConnections;
}
