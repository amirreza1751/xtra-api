package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.AdvancedStreamOptions;
import com.xtra.api.model.StreamType;
import com.xtra.api.projection.admin.epg.EpgDetails;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelInsertView {
    private Long id;
    //todo
    @NotBlank(message = "channel name can not be empty")
    private String name;
    private String logo;
    @NotNull(message = "streamType can not be empty")
    private StreamType streamType;
    private String streamToken;
    private String notes;


    private Set<DayOfWeek> daysToRestart;
    private LocalTime timeToRestart;
    private List<String> streamInputs;

    private Set<Long> servers= new HashSet<>();
    private Set<Long> collections= new HashSet<>();
    private EpgDetails epgDetails;
    private AdvancedStreamOptions advancedStreamOptions;
}
