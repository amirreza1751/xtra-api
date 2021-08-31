package com.xtra.api.projection.admin.epg;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class EpgView {
    private Long id;
    private String name;
    private String source;
    private LocalDateTime lastUpdated;

    private Set<EpgChannelView> channels;
}
