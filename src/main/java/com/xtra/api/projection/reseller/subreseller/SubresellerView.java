package com.xtra.api.projection.reseller.subreseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SubresellerView {
    private long id;
    private String username;
    private String email;
    private int credits;
    private String resellerDns;
    private String notes;

}
