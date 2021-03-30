package com.xtra.api.projection.admin.line;

import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineInsertView {
    protected String username;
    @NotNull(message = "Password is mandatory")
    protected String password;
    @NotNull(message = "Role is mandatory")
    private Long roleId;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isTrial;
    private boolean isBanned = false;
    private boolean isBlocked = false;
    private String adminNotes;
    private String resellerNotes;

    private List<StreamProtocol> allowedOutputs;
    private Long ownerId;
    private LinkedHashSet<Long> collections;

    /* Location Based Permissions */
    private boolean isCountryLocked = false;
    private String forcedCountry;

    private List<String> allowedIps;
    private List<String> blockedIps;

    private List<String> allowedUserAgents;
    private List<String> blockedUserAgents;
}
