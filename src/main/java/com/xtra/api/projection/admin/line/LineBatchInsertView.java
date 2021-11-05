package com.xtra.api.projection.admin.line;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.stream.StreamProtocol;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
