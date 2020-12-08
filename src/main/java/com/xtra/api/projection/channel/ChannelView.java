package com.xtra.api.projection.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamInput;
import com.xtra.api.model.StreamType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelView {

    private Long id;
    private String name;
    private String logo;
    private StreamType streamType;
    private String streamToken;
    private boolean readNative = false;
    private boolean streamAll = false;
    private boolean directSource = false;
    private boolean genTimestamps = false;
    private boolean rtmpOutput = false;
    private String userAgent;
    private String notes;

    private Set<DayOfWeek> daysToRestart;
    private LocalTime timeToRestart;
    private String customFFMPEG;
    private List<String> streamInputs;

    private Set<Long> servers;
    private Set<Long> collections;
}
