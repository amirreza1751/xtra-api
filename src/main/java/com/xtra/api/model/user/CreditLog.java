package com.xtra.api.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class CreditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String actorUsername;
    private UserType actorType;
    private String targetUsername;
    private int initialCredits;
    private int finalCredits;
    private int changeAmount;

    private LocalDateTime date;

    private CreditLogReason reason;

    @Column(columnDefinition = "text")
    private String description;

    public CreditLog(String actorUsername, UserType actorType, String targetUsername, int initialCredits, int finalCredits, int changeAmount, LocalDateTime date, CreditLogReason reason, String description) {
        this.actorUsername = actorUsername;
        this.actorType = actorType;
        this.targetUsername = targetUsername;
        this.initialCredits = initialCredits;
        this.finalCredits = finalCredits;
        this.changeAmount = changeAmount;
        this.date = date;
        this.reason = reason;
        this.description = description;
    }
}
