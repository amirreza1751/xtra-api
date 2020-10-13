package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.Period;
import java.util.List;

@Entity
@Data
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isTrial;
    private int credits;
    private Period duration;
    private int maxConnections;
    private boolean canRestream;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<StreamProtocol> allowedOutputs;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private DownloadList defaultDownloadList;

}
