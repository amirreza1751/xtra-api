package com.xtra.api.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
