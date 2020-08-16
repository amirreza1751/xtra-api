package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
    @JsonBackReference
    private Stream stream;

    public StreamInfo() {
    }

    public StreamInfo(Stream stream) {
        this.stream = stream;
    }
}


