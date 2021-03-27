package com.xtra.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class StreamInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uptime;
    private String currentInput;
    private String resolution;
    private String videoCodec;
    private String audioCodec;

    @ToString.Exclude
    @OneToOne(mappedBy = "streamInfo", cascade = CascadeType.ALL)
    private StreamServer streamServer;

    public StreamInfo(String uptime, String currentInput, String resolution, String videoCodec, String audioCodec) {
        this.uptime = uptime;
        this.currentInput = currentInput;
        this.resolution = resolution;
        this.videoCodec = videoCodec;
        this.audioCodec = audioCodec;
    }
}


