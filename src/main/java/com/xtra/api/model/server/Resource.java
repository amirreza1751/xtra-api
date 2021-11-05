package com.xtra.api.model.server;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString(exclude = {"server"})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cpuMaxFreq;
    private double cpuLoad;

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
