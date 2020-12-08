package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "username")
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String lineToken;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isReStreamer = false;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isBanned = false;
    private boolean isIspLocked = false;
    private boolean isStalker;
    private String adminNotes;
    private String resellerNotes;

    private boolean isMag;
    private boolean isEnigma;
    private boolean isMinistraPortal;
    private String mac;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<StreamProtocol> allowedOutputs;

    @ManyToOne(cascade = CascadeType.ALL)
    private User referrer;

    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private DownloadList defaultDownloadList;

    @OneToMany(mappedBy = "line")
    @JsonManagedReference("line_id")
    @ToString.Exclude
    private List<LineActivity> activities = new ArrayList<>();

    public int getCurrentConnections() {
        return activities.size();
    }

}
