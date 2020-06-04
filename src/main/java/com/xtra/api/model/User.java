package com.xtra.api.model;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String username;
    private String password;
    private String email;
    private String _2FASec;
    private boolean isBanned = false;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToOne
    private Role role;
}
