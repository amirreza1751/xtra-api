package com.xtra.api.model;

import lombok.Data;
import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

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
    protected UserType userType;

    @ManyToOne
    protected Role role;
}
