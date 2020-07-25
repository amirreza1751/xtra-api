package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class StreamInfo {
    @Id
    private Long streamId;
    private String uptime;
    private String currentInput;
    private String resolution;
    private String videoCodec;
    private String audioCodec;

    @OneToOne
    @MapsId
    private Stream stream;

    public StreamInfo() {
    }

}
