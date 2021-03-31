package com.xtra.api.projection.admin.user.reseller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.projection.admin.user.UserView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResellerView extends UserView {
    private boolean isVerified;
    private int credits;
    private String resellerDns;
    private String notes;
    private String lang;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime lastLoginDate;
    private String lastLoginIp;
    private Set<Long> customDownloadListIds;
    private Long ownerId;
}
