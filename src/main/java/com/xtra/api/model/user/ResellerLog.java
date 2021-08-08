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

    @OneToOne
    private Reseller reseller;

    @OneToOne
    private User user;

    @OneToOne
    private CreditLog creditLog;

    private LocalDateTime date;

    private ResellerLogAction action;

    public ResellerLog(Reseller reseller,User user, CreditLog creditLog, LocalDateTime date, ResellerLogAction action) {
        this.user = user;
        this.reseller = reseller;
        this.creditLog = creditLog;
        this.action = action;
        this.date = date;
    }

    public ResellerLog(Reseller reseller,User user, LocalDateTime date, ResellerLogAction action) {
        this.user = user;
        this.reseller = reseller;
        this.action = action;
        this.date = date;
    }
}
