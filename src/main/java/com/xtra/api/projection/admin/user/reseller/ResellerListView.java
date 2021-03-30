package com.xtra.api.projection.admin.user.reseller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.projection.admin.user.UserSimpleView;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResellerListView extends UserSimpleView {
    private boolean isVerified;
    private int credits;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime lastLoginDate;
    private String lastLoginIp;
    private String ownerUsername;
}
