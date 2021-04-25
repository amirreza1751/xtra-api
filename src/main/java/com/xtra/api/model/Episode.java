package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Episode{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int episodeNumber;
    private String episodeName;
    private String notes;
    @URL
    private String imageUrl;
    @Column(columnDefinition = "TEXT")
    private String plot;
    private LocalDate releaseDate;
    private int runtime;
    private float rating;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Video> videos;
}
