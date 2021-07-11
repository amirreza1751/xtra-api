package com.xtra.api.projection.admin.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.model.user.CreditLogReason;
import com.xtra.api.model.user.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreditLogView {
    private Long actorId;
    private String actorUsername;
    private UserType actorUserType;
    private Long targetId;
    private String targetUsername;
    private int initialCredits;
    private int finalCredits;
    private int changeAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime date;
    private String reason;
    private String description;
}
