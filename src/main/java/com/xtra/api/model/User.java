package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(unique = true)
    protected String username;
    protected String password;

    @Email(message = "Email should be valid", regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    @NotEmpty
    @NotNull
    protected String email;
    protected String _2FASec;
    protected boolean isBanned = false;

    @Enumerated(EnumType.STRING)
    protected UserType userType = UserType.USER;

    @ManyToOne
    protected Role role;
}
