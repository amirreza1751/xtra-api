package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.stream.AdvancedStreamOptions;
import com.xtra.api.model.stream.StreamType;
import com.xtra.api.projection.admin.epg.EpgDetails;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelImportView {
    private MultipartFile document;

    private String notes;
    private Set<DayOfWeek> daysToRestart;
    private LocalTime timeToRestart;
    private Set<Long> servers;
    private Set<Long> collections;
    private AdvancedStreamOptions advancedStreamOptions;
    private Long onDemand;
}
