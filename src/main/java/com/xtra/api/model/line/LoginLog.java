package com.xtra.api.model.line;

import com.xtra.api.model.user.User;
import com.xtra.api.model.user.UserType;
import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private UserType type;
    private String ip;
    private LoginLogStatus status;
    private LocalDateTime date;

    public LoginLog(String username, UserType type, String ipAddress, LoginLogStatus success, LocalDateTime now) {
        this.username = username;
        this.type = type;
        this.ip = ipAddress;
        this.status = success;
        this.date = now;
    }

    public LoginLog() {

    }
}
