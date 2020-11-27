package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
    private String name;

    @OneToMany(mappedBy = "defaultDownloadList")
    private List<Line> lines;

    @JsonIgnore
    @OneToOne(mappedBy = "defaultDownloadList")
    private Package _package;

    @ManyToOne
    private Reseller owner;

    @JsonManagedReference("dl")
    @OneToMany(mappedBy = "downloadList", cascade = {CascadeType.PERSIST,CascadeType.DETACH,CascadeType.REFRESH,CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("order ASC")
    Set<DownloadListCollection> collectionsAssign;

}
