package com.xtra.api.projection.admin.downloadlist;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class DownloadListInsertView {
    @NotBlank(message = "Download List name can not be empty")
    private String name;

    @NotNull(message = "Download collections can not be empty")
    private Set<Long> collections;
}
