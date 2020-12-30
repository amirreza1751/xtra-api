package com.xtra.api.projection.user.reseller;

import com.xtra.api.model.DownloadList;
import com.xtra.api.projection.user.UserInsertView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResellerInsertView extends UserInsertView {
    private boolean isVerified;
    private int credits;
    private String resellerDns;
    private String notes;
    private String lang;

    private Long owner;
}
