package com.xtra.api.projection.reseller.line;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineUpdateView {
    protected String username;
    protected String password;
    private String resellerNotes;
    private LinkedHashSet<Long> collections;

    /* Location Based Permissions */
    private boolean isCountryLocked = false;
    private String forcedCountry;

    private List<String> allowedIps;
    private List<String> blockedIps;

    private List<String> allowedUserAgents;
    private List<String> blockedUserAgents;
}
