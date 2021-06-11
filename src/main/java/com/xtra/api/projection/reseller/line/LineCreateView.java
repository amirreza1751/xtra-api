package com.xtra.api.projection.reseller.line;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class LineCreateView extends LineUpdateView {
    @NotNull(message = "package Id can not be empty")
    private Long packageId;
}
