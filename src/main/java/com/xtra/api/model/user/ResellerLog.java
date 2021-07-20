package com.xtra.api.model.user;

import com.xtra.api.model.line.Line;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Line line;

    @OneToOne
    private CreditLog creditLog;

    private LocalDateTime date;

    private ResellerLogAction action;

    public ResellerLog(Line line, Reseller reseller, CreditLog creditLog, LocalDateTime date, ResellerLogAction action) {
        this.line = line;
        this.reseller = reseller;
        this.creditLog = creditLog;
        this.action = action;
        this.date = date;
    }
}
