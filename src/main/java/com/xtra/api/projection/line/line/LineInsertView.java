package com.xtra.api.projection.line.line;

import lombok.Data;

import java.util.LinkedHashSet;

@Data
public class LineInsertView {
    protected String username;
    protected String password;
    private LinkedHashSet<Long> collections;

    /* Location Based Permissions */
    private boolean isCountryLocked = false;
    private String forcedCountry;
}
