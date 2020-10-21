package com.xtra.api.projection;

import com.xtra.api.model.StreamInput;
import com.xtra.api.model.StreamType;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class ChannelInsertView {
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
    private List<StreamInput> streamInputs;

    private ArrayList<Long> servers;
}
