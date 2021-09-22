package com.xtra.api.projection.admin.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.model.user.UserType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResellerLogView {
    private String resellerUsername;
    private String action;
    private String targetUsername;
    private UserType targetType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime date;
}
