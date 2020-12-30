package com.xtra.api.projection.user.reseller;

import com.xtra.api.projection.user.UserView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResellerView extends UserView {
    private boolean isVerified;
    private int credits;
    private String resellerDns;
    private String notes;
    private String lang;

    private Set<Long> customDownloadLists;

    private Long owner;
}
