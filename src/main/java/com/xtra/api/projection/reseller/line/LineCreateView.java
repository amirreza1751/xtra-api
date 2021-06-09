package com.xtra.api.projection.reseller.line;

import com.xtra.api.model.StreamProtocol;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class LineCreateView extends LineUpdateView {
    private Long packageId;
}
