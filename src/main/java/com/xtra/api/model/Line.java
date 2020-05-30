package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Data
public class Line {
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
