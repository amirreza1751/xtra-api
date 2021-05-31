package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seasonName;
    private int seasonNumber;
    private int NoOfEpisodes;
    private LocalDate airDate;


    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @OrderBy("episodeNumber")
    private List<Episode> episodes;
}
