package com.xtra.api.projection.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StreamInputPair {

    private String oldDns;
    private String newDns;

    public StreamInputPair(String oldDns, String newDns) {
        this.oldDns = oldDns;
        this.newDns = newDns;
    }

    public StreamInputPair() {
    }


}
