package com.xtra.api.model.server;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"server"})
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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
