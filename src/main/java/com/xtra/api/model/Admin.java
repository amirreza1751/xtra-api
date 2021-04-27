package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Admin extends User {

    @NotBlank(message = "Admin firstname must not be empty")
    private String firstname;
    @NotBlank(message = "Admin lastname must not be empty")
    private String lastname;

    public Admin() {
        setUserType(UserType.ADMIN);
    }
}
