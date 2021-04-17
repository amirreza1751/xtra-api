package com.xtra.api.projection.admin.line;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineBatchInsertView {
    private Set<Long> lineIds;

    private String neverExpire;
    private int maxConnections = 0;
    private String isTrial;
    private String isBanned;
    private String isBlocked;
    private String adminNotes;

    private List<StreamProtocol> allowedOutputs;
}
