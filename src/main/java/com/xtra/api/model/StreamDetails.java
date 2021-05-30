package com.xtra.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uptime;
    private String currentInput;
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private String speed;
    private String frameRate;
    private String bitrate;
    private StreamStatus streamStatus;

    @ToString.Exclude
    @OneToOne(mappedBy = "streamDetails")
    private StreamServer streamServer;

    public StreamDetails(String uptime, String currentInput, String resolution, String videoCodec, String audioCodec, String speed, String frameRate, String bitrate, StreamStatus streamStatus) {
        this.uptime = uptime;
        this.currentInput = currentInput;
        this.resolution = resolution;
        this.videoCodec = videoCodec;
        this.audioCodec = audioCodec;
        this.speed = speed;
        this.frameRate = frameRate;
        this.bitrate = bitrate;
        this.streamStatus = streamStatus;
    }

}


