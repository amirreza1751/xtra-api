package com.xtra.api.projection.line;

import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineInsertView {
    protected String username;
    protected String password;
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

    private List<StreamProtocol> allowedOutputs;
    private Long owner;
    private LinkedHashSet<Long> collections;
}
