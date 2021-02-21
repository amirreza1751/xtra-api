package com.xtra.api.projection.admin.epg;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EpgSimpleView {
    private String name;
    private String source;
    private LocalDateTime lastUpdated;
}
