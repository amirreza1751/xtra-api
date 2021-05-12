package com.xtra.api.projection.line.line;


import lombok.Data;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;


@Data
public class LineInsertView {

    @NotBlank(message = "line username can not be empty")
    protected String username;
    @NotBlank(message = "line password can not be empty")
    protected String password;
    @NotNull(message = "collection list can not be empty")
    private LinkedHashSet<Long> collections;

    /* Location Based Permissions */
    private boolean isCountryLocked = false;
    private String forcedCountry;
}
