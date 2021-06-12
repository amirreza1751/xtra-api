package com.xtra.api.model.user;

import com.xtra.api.model.user.Reseller;
import com.xtra.api.model.user.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class CreditChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User actor;

    @OneToOne
    private Reseller target;
    private int changeAmount;
    private LocalDateTime date;

    @Column(columnDefinition = "text")
    private String description;
}
