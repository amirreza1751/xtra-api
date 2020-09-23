package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Admin extends User {
    private String firstName;
    private String lastName;

    public Admin() {
        setUserType(UserType.ADMIN);
    }
}
