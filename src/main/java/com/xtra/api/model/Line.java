package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Line extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDateTime expireDate;
    private int maxConnections;
    private boolean isReStreamer;
    private boolean isTrial;
    private boolean isBlocked;
    private boolean isAdminBlocked;
    private boolean isIspLocked;
    private boolean isStalker;
    private String notes;

    @ManyToOne
    private User referrer;
    @ManyToOne
    private User reseller;

}
