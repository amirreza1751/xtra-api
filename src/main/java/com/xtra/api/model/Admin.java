package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Admin extends User {
    private String firstname;
    private String lastname;

    public Admin() {
        setUserType(UserType.ADMIN);
    }
}
