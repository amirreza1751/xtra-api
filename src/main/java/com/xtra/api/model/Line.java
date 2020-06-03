package com.xtra.api.model;

import com.sun.istack.NotNull;
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
    private int maxConnections = 1;
    private boolean isReStreamer = false;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isAdminBlocked = false;
    private boolean isIspLocked = false;
    private boolean isStalker;
    private String notes;

    @ManyToOne
    private User referrer;
    @ManyToOne
    private User reseller;

}
