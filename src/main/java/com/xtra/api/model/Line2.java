package com.xtra.api.model;

import com.neovisionaries.i18n.CountryCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Line2 extends User {

    private String lineToken;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isReStreamer = false;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isBanned = false;
    private boolean isStalker;
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

    private boolean isMag;
    private boolean isEnigma;
    private boolean isMinistraPortal;
    private String mac;

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
    private List<LineActivity> activities = new ArrayList<>();

    public int getCurrentConnections() {
        return activities.size();
    }

}