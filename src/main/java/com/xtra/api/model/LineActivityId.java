package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineActivityId implements Serializable {
    private Long lineId;
    private Long streamId;
    private Long serverId;
    private String userIp;


    public LineActivityId() {}
    public LineActivityId(Long lineId, Long streamId, Long serverId, String userIp){
        this.lineId = lineId;
        this.streamId = streamId;
        this.serverId = serverId;
        this.userIp = userIp;
    }
}
