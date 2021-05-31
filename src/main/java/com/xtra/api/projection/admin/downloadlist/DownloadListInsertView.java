package com.xtra.api.projection.admin.downloadlist;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class DownloadListInsertView {
    private Long id;
    @NotBlank(message = "Download List name can not be empty")
    private String name;

    private Set<Long> collections;
}
