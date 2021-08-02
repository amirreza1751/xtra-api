package com.xtra.api.model.line;

import com.neovisionaries.i18n.CountryCode;
import com.xtra.api.model.download_list.DownloadList;
import com.xtra.api.model.mag.MagDevice;
import com.xtra.api.model.stream.StreamProtocol;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.model.user.User;
import com.xtra.api.model.user.UserType;
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

    @OneToOne
    private MagDevice magDevice;

    public Line() {
        userType = UserType.LINE;
    }

    public boolean isExpired() {
        return (!neverExpire && expireDate.isBefore(LocalDateTime.now()));
    }
}
