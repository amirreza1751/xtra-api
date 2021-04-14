package com.xtra.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ConnectionId implements Serializable {
    private Long lineId;
    private Long streamId;
    private Long serverId;
    private String userIp;
}
