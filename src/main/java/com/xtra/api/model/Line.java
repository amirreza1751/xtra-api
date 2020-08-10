package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "username")
public class Line extends User {
    private String lineToken;
    private LocalDate expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isReStreamer = false;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isIspLocked = false;
    private boolean isStalker;
    private String adminNotes;
    private String resellerNotes;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<StreamProtocol> allowedOutputs;

    @ManyToOne(cascade = CascadeType.ALL)
    private User referrer;

    @ManyToOne(cascade = CascadeType.ALL)
    private User owner;

    @OneToMany(mappedBy = "line")
    private List<LineActivity> activities = new ArrayList<>();

    public int getCurrentConnections() {
        return activities.size();
    }

}
