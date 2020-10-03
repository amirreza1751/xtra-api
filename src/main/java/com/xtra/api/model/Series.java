package com.xtra.api.model;

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

    @ManyToMany(mappedBy = "series")
    private List<Collection> collections;
}
