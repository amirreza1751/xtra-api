package com.xtra.api.model;

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
