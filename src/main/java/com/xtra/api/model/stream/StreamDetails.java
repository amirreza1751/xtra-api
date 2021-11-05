package com.xtra.api.model.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
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
    private LocalDateTime updated;

    @ToString.Exclude
    @OneToOne(mappedBy = "streamDetails")
    private StreamServer streamServer;

    public StreamDetails() {
        updated = LocalDateTime.now();
    }

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
        updated = LocalDateTime.now();
    }

}


