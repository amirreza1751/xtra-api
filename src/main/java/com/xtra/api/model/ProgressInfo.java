package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class ProgressInfo {
    @Id
    private Long stream_id;
    private String speed;
    private String frameRate;
    private String bitrate;

    public ProgressInfo() {

    }

    public ProgressInfo(Long stream_id) {
        this.stream_id = stream_id;
    }
}
