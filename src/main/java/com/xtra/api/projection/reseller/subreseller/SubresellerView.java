package com.xtra.api.projection.reseller.subreseller;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SubresellerView {
    private long id;
    private String username;
    private String email;
    private int credits;
    private String resellerDns;
    private String notes;

}
