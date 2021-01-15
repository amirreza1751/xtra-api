package com.xtra.api.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
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
}


