package com.xtra.api.projection.admin.line;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineBatchDeleteView {
    @NotNull(message = "line Ids list can not be empty")
    private Set<Long> lineIds;
}
