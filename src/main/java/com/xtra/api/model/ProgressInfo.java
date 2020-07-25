package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Entity
@Data
public class ProgressInfo {
    @Id
    private Long streamId;
    private String speed;
    private String frameRate;
    private String bitrate;

    @OneToOne
    @MapsId
    @JsonBackReference
    private Stream stream;

    public ProgressInfo() {

    }

    public ProgressInfo(Long streamId) {
        this.streamId = streamId;
    }
}
