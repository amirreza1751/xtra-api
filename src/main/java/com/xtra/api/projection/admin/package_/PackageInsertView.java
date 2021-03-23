package com.xtra.api.projection.admin.package_;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamProtocol;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Period;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PackageInsertView {
    private String name;
    private boolean isTrial = false;
    private int credits;
    private Period duration;
    private int maxConnections;
    private boolean canRestream = false;

    private List<StreamProtocol> allowedOutputs;

    private LinkedHashSet<Long> collections;

    private Set<Long> allowedRoles;
}
