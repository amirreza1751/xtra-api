package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ProgressInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String speed;
    private String frameRate;
    private String bitrate;

    @OneToOne(mappedBy = "progressInfo", cascade = CascadeType.ALL)
    private StreamServer streamServer;
}
