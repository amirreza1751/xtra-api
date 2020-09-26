package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class StreamServerId implements Serializable {
    private Long streamId;
    private Long serverId;

    public StreamServerId() {
    }

    public StreamServerId(Long streamId, Long serverId) {
        this.streamId = streamId;
        this.serverId = serverId;
    }
}
