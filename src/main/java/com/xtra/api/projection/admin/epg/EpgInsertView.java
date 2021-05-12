package com.xtra.api.projection.admin.epg;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EpgInsertView {
    @NotNull(message = "EPG name can not be empty")
    private String name;
    @NotNull(message = "EPG source can not be empty")
    private String source;
}
