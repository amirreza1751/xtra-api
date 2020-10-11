package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class DownloadList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean systemDefault;

    @ManyToOne
    private Reseller owner;

    @JsonManagedReference("dl")
    @OneToMany(mappedBy = "downloadList", cascade = {CascadeType.PERSIST,CascadeType.REFRESH})
    @OrderBy("order ASC")
    Set<DownloadListCollection> collectionsAssign;

}
