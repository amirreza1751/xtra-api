package com.xtra.api.projection.admin.series;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.SeriesInfo;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SeriesInsertView {
    private Long id;
    @NotBlank(message = "Series name can not be empty")
    private String name;
    private String year;

    private SeriesInfo info;
    @NotNull(message = "series collections can not be empty")
    private Set<Long> collections;
}
