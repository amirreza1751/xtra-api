package com.xtra.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"collectionsAssign", "lines"})
public class DownloadList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "defaultDownloadList")
    private List<Line> lines;

    @OneToOne(mappedBy = "defaultDownloadList")
    private Package _package;

    @ManyToOne
    private Reseller owner;

    @OneToMany(mappedBy = "downloadList", cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.MERGE}, orphanRemoval = true)
    @OrderBy("order ASC")
    Set<DownloadListCollection> collectionsAssign;

}
