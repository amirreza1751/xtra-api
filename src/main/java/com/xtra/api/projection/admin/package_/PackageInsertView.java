package com.xtra.api.projection.admin.package_;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamProtocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Period;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PackageInsertView {
    @NotBlank( message = "Package name can not be empty" )
    private String name;
    private boolean isTrial = false;
    @Digits(message = "credit is a 5 digit and maximum 3 fractions", integer = 5, fraction = 3)
    private int credits;
    private Period duration;
    private int maxConnections;
    private boolean canRestream = false;

    @NotNull(message = "package supported protocol list can not be empty")
    private List<StreamProtocol> allowedOutputs;

    private LinkedHashSet<Long> collections;

    @NotNull(message = "allowed roles is mandatory")
    private Set<Long> allowedRoles;
}
