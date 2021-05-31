package com.xtra.api.projection.admin.line;

import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineInsertView {
    @NotBlank(message = "username is mandatory")
    protected String username;
    @NotBlank(message = "Password is mandatory")
    protected String password;
    @NotNull(message = "Role is mandatory")
    private Long roleId;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    @PositiveOrZero(message = "max connections number should be a positive number")
    private int maxConnections = 1;
    private boolean isTrial;
    private boolean isBanned = false;
    private boolean isBlocked = false;
    private String adminNotes;
    private String resellerNotes;

    @NotNull(message = "allowed Protocols list can not be empty")
    private List<StreamProtocol> allowedOutputs;
    @NotNull(message = "owner is mandatory")
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
