package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Reseller extends User {
    private boolean isVerified;
    private int credits;
    private String resellerDns;
    private String notes;
    private String lang;

    @OneToMany(mappedBy = "owner")
    private List<DownloadList> customDownloadLists;

    public Reseller() {
        setUserType(UserType.RESELLER);
    }

    @ManyToOne
    private Reseller owner;
}
