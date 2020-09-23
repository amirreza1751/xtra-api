package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Reseller extends User {
    private boolean isVerified;
    private int credits;
    private String resellerDns;
    private String notes;
    private String lang;

    public Reseller() {
        setUserType(UserType.RESELLER);
    }

    @ManyToOne
    private Reseller owner;
}
