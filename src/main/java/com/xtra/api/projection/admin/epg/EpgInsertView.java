package com.xtra.api.projection.admin.epg;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Data
public class EpgInsertView {
    @NotBlank(message = "EPG name can not be empty")
    private String name;
    @URL(message = "EPG source should be a valid URL ")
    private String source;
}
