package com.xtra.api.projection.admin.user.reseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResellerBatchView {
    private String credits;
    private String resellerDns;
    private String notes;
    private String lang;
    private Long ownerId;
    protected String isBanned;
    protected Long roleId;

    Set<Long> ids;
}
