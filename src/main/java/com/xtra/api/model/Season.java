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

    private int seasonNumber;
    private int NoOfEpisodes;
    private LocalDate airDate;

    @OneToMany
    private List<Episode> episodes;
}