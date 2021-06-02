package com.xtra.api.model;

import com.neovisionaries.i18n.CountryCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = false)
@Entity
@Data
public class Line extends User {
    private String lineToken;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private boolean isTrial;
    private boolean isBlocked = false;
    private String adminNotes;
    private String resellerNotes;

    private boolean isCountryLocked = false;
    private CountryCode forcedCountry;

    @ElementCollection
    private List<String> allowedIps;
    @ElementCollection
    private List<String> blockedIps;

    @ElementCollection
    private List<String> allowedUserAgents;
    @ElementCollection
    private List<String> blockedUserAgents;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<StreamProtocol> allowedOutputs;

    @ManyToOne
    private User referrer;

    @ManyToOne
    private Reseller owner;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private DownloadList defaultDownloadList;

    @OneToMany(mappedBy = "line")
    @ToString.Exclude
    private List<Connection> connections = new ArrayList<>();

    public Line() {
        userType = UserType.LINE;
    }
}
