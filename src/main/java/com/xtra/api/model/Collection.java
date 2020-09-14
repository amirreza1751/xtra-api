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

    @ManyToMany(mappedBy = "collections")
    private List<Package> packages;

    @ManyToMany(mappedBy = "collections")
    private List<Channel> channels;

    @ManyToMany(mappedBy = "collections")
    private List<Movie> movies;
}
