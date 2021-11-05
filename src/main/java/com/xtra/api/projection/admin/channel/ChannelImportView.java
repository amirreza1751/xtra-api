package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChannelImportView {
    private MultipartFile document;

    private String notes;
    private Set<DayOfWeek> daysToRestart;
    private String timeToRestart;
    private Set<Long> servers;
    private Set<Long> collections;
    private String advancedStreamOptions;
    private Long onDemand;
}
