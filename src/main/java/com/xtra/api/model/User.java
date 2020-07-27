package com.xtra.api.model;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected long id;
    protected String username;
    protected String password;
    protected String email;
    protected String _2FASec;
    protected boolean isBanned = false;

    @Enumerated(EnumType.STRING)
    protected UserType userType;

    @ManyToOne
    protected Role role;
}
