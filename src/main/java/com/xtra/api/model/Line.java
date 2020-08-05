package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Line extends User {
    private LocalDateTime expireDate;
    private int maxConnections = 1;
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

    @ManyToOne
    private User referrer;

    @ManyToOne
    private User owner;


}
