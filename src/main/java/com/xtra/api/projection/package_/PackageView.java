package com.xtra.api.projection.package_;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamProtocol;
import com.xtra.api.projection.downloadlist.DlCollectionView;
import lombok.Data;

import java.time.Period;
import java.util.List;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PackageView {
    private Long id;
    private String name;
    private boolean isTrial;
    private int credits;
    private Period duration;
    private int maxConnections;
    private boolean canRestream;

    private List<StreamProtocol> allowedOutputs;

    private Set<DlCollectionView> collections;

    private Set<Long> allowedRoles;
}