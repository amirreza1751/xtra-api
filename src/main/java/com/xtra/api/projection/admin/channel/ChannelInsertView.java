package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.AdvancedStreamOptions;
import com.xtra.api.model.StreamType;
import com.xtra.api.projection.admin.epg.EpgDetails;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelInsertView {
    private Long id;
    private String name;
    private String logo;
    private StreamType streamType;
    private String streamToken;
    private String notes;

    private Set<DayOfWeek> daysToRestart;
    private LocalTime timeToRestart;
    private List<String> streamInputs;

    private Set<Long> servers;
    private Set<Long> collections;
    private EpgDetails epgDetails;
    private AdvancedStreamOptions advancedStreamOptions;
}
