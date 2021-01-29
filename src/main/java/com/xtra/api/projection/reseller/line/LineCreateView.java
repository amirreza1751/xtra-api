package com.xtra.api.projection.reseller.line;

import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineCreateView {
    protected String username;
    protected String password;
    private String resellerNotes;

    private Long packageId;
    private LinkedHashSet<Long> collections;

    /* Location Based Permissions */
    private boolean isCountryLocked = false;
    private String forcedCountry;

    private List<String> allowedIps;
    private List<String> blockedIps;

    private List<String> allowedUserAgents;
    private List<String> blockedUserAgents;
}
