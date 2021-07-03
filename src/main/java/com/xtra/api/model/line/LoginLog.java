package com.xtra.api.model.line;

import com.xtra.api.model.user.User;
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
    @ManyToOne
    private User user;
    private String ip;
    private LoginLogStatus status;
    private LocalDateTime date;

    public LoginLog(User user, String ipAddress, LoginLogStatus success, LocalDateTime now) {
        this.user = user;
        this.ip = ipAddress;
        this.status = success;
        this.date = now;
    }

    public LoginLog() {

    }
}
