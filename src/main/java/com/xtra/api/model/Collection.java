package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isDefaultColl;

    @ManyToOne
    private Reseller owner;

    @Enumerated(EnumType.STRING)
    private CollectionType type;

    @JsonManagedReference("collection")
    @OneToMany(mappedBy = "downloadList")
    private List<DownloadListCollection> collectionsAssign;

    @ManyToMany(mappedBy = "collections")
    private List<Package> packages;


    @ManyToMany
    private List<Channel> channels;

    @ManyToMany
    private List<Movie> movies;

    @ManyToMany
    private List<Series> series;

    @ManyToMany
    private List<Radio> radios;
}
