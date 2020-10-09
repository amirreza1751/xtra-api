package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany
    private List<Episode> episodes;

    //@JsonBackReference("series")
    @ManyToMany(mappedBy = "series")
    private List<Collection> collections;

    public Series() {

    }

    public Series(Long id) {
        this.id = id;
    }
}
