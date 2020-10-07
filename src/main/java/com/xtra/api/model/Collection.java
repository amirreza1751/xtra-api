package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isSystemDefault;

    @ManyToOne
    private Reseller owner;

    @Enumerated(EnumType.STRING)
    private CollectionType type;

    @JsonManagedReference("dlc")
    @OneToMany(mappedBy = "collection")
    private List<DownloadListCollection> downloadListCollections;

    @ManyToMany(mappedBy = "collections")
    private List<Package> packages;


    //@JsonManagedReference("channels")
    @ManyToMany
    private List<Channel> channels;

    //@JsonManagedReference("movies")
    @ManyToMany
    private List<Movie> movies;

    //@JsonManagedReference("series")
    @ManyToMany
    private List<Series> series;

    //@JsonManagedReference("radios")
    @ManyToMany
    private List<Radio> radios;
}
