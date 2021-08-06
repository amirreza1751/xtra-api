package com.xtra.api.model.mag;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class MagEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int status;
    private String event;
    private boolean needsConfirm;
    private String msg;
    private boolean rebootAfterOk;
    private boolean autoHideTimeout;
    private boolean additionalServicesOn;
    private boolean anec;
    private boolean vclub;

    @ManyToOne
    private MagDevice magDevice;

}
