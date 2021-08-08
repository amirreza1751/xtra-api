package com.xtra.api.model.user;

import com.xtra.api.model.line.Line;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class ResellerLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resellerUsername;
    private String targetUsername;
    private UserType targetType;
    private LocalDateTime date;
    private ResellerLogAction action;

    public ResellerLog(String resellerUsername,String targetUsername, UserType targetType, LocalDateTime date, ResellerLogAction action) {
        this.resellerUsername = resellerUsername;
        this.targetUsername = targetUsername;
        this.targetType = targetType;
        this.action = action;
        this.date = date;
    }
}
