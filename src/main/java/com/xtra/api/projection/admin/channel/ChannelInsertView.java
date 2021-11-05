package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.stream.AdvancedStreamOptions;
import com.xtra.api.model.stream.StreamType;
import com.xtra.api.projection.admin.epg.EpgDetails;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChannelInsertView {
    @NotBlank(message = "channel name can not be empty")
    private String name;
    @NotBlank(message = "channel logo can not be empty")
    private String logo;
    @NotNull(message = "streamType can not be empty")
    private StreamType streamType;
    private String notes;

    private Set<DayOfWeek> daysToRestart;
    private LocalTime timeToRestart;
    @NotNull(message = "stream Inputs can not be empty")
    private List<String> streamInputs;

    @NotNull(message = "servers can not be empty")
    private Set<Long> servers;
    @NotNull(message = "collections can not be empty")
    private Set<Long> collections;
    private Set<Long> categories;
    private EpgDetails epgDetails;
    private AdvancedStreamOptions advancedStreamOptions;

    private Long catchUp;
    private int catchUpDays;
    private Long onDemand;
}
