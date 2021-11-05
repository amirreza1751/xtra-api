package com.xtra.api.projection.admin.package_;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.stream.StreamProtocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.Period;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PackageInsertView {
    @NotBlank( message = "Package name can not be empty" )
    private String name;
    private boolean isTrial = false;
    @PositiveOrZero(message = "credits should be a positive number")
    private int credits;
    private Period duration;
    @PositiveOrZero(message = "max connections number should be a positive number")
    private int maxConnections;
    private boolean canRestream = false;

    @NotNull(message = "package supported protocol list can not be empty")
    private List<StreamProtocol> allowedOutputs;

    private LinkedHashSet<Long> collections;

    @NotNull(message = "allowed roles is mandatory")
    private Set<Long> allowedRoles;
}
