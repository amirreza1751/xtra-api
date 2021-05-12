package com.xtra.api.projection.reseller.line;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineCreateView {

    @NotBlank(message = "line username can not be empty")
    protected String username;
    @NotBlank(message = "line password can not be empty")
    protected String password;
    private String resellerNotes;

    private Long packageId;
    @NotNull(message = "collection list can not be empty")
    private LinkedHashSet<Long> collections;

    /* Location Based Permissions */
    private boolean isCountryLocked = false;
    private String forcedCountry;

    private List<String> allowedIps;
    private List<String> blockedIps;

    private List<String> allowedUserAgents;
    private List<String> blockedUserAgents;
}
