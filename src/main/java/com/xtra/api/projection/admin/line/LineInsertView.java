package com.xtra.api.projection.admin.line;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.model.StreamProtocol;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineInsertView {
    protected String username;
    @NotNull(message = "Password is mandatory")
    protected String password;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    @PositiveOrZero(message = "max connections number should be a positive number")
    private int maxConnections = 1;
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
