package com.xtra.api.model.vod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties({"series"})
@ToString(exclude = {"series"})
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

    @ManyToOne
    private Series series;
}
