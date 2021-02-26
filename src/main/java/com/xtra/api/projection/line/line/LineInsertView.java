package com.xtra.api.projection.line.line;

import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@Data
public class LineInsertView {
    protected String username;
    protected String password;
    private LinkedHashSet<Long> collections;

    /* Location Based Permissions */
    private boolean isCountryLocked = false;
    private String forcedCountry;
}
