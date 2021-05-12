package com.xtra.api.projection.admin.line;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineBatchInsertView {
    @NotNull(message = "line Ids list can not be empty")
    private Set<Long> lineIds;

    private String neverExpire;
    private int maxConnections = 0;
    @NotBlank(message = "is Trial can not be empty")
    private String isTrial;
    @NotBlank(message = "is Banned can not be empty")
    private String isBanned;
    @NotBlank(message = "is Blocked can not be empty")
    private String isBlocked;
    private String adminNotes;

    private List<StreamProtocol> allowedOutputs;
}
