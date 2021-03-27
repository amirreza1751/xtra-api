package com.xtra.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class ProgressInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String speed;
    private String frameRate;

    public ProgressInfo(String speed, String frameRate, String bitrate) {
        this.speed = speed;
        this.frameRate = frameRate;
        this.bitrate = bitrate;
    }

    private String bitrate;

    @OneToOne(mappedBy = "progressInfo", cascade = CascadeType.ALL)
    private StreamServer streamServer;
}
