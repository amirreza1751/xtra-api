package com.xtra.api.model.user;

import com.xtra.api.model.download_list.DownloadList;
import com.xtra.api.model.line.Line;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.neovisionaries.i18n.CountryCode;

import javax.persistence.ElementCollection;
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

    @ElementCollection
    private List<CountryCode> mainMarket;

    private String telegramId;

    @OneToMany(mappedBy = "owner")
    private List<DownloadList> customDownloadLists;

    @ManyToOne
    private Reseller owner;

    @OneToMany(mappedBy = "owner")
    private List<Line> lines;

    public Reseller() {
        setUserType(UserType.RESELLER);
    }
}
