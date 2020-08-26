package com.xtra.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LineActivityId implements Serializable {
    private Long lineId;
    private Long streamId;
    private String userIp;

    public LineActivityId() {

    }
}
