package com.xtra.api.projection.admin.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.model.line.LoginLogStatus;
import com.xtra.api.model.stream.StreamProtocol;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginLogView {
    private String username;
    private String userId;
    private String ip;
    private LoginLogStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime date;
}
