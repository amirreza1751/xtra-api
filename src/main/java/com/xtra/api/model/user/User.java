package com.xtra.api.model.user;

import com.xtra.api.model.role.Role;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.ZonedDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(unique = true)
    protected String username;
    protected String password;

    @Email(message = "Email should be valid", regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
    protected String email;
    protected String _2FASec;
    protected boolean isUsing2FA;
    protected boolean isBanned = false;

    protected ZonedDateTime lastLoginDate;
    protected String lastLoginIp;

    @Enumerated(EnumType.STRING)
    protected UserType userType = UserType.LINE;

    @ManyToOne
    protected Role role;
}
