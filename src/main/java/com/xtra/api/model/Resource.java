package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cpuMaxFreq;
    @ElementCollection
    private List<Double> cpuCurrentFreq;

    private double memoryTotal;
    private double memoryAvailable;

    private String networkName;
    private Long networkBytesSent;
    private Long networkBytesRecv;

    private int connections;

    private Long upTime;

    @OneToOne(mappedBy = "resource")
    private Server server;

}
