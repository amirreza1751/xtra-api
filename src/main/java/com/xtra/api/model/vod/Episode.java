package com.xtra.api.model.vod;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xtra.api.model.vod.Season;
import com.xtra.api.model.vod.Video;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@JsonIgnoreProperties({"season"})
@ToString(exclude = {"season"})
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

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<Video> videos;

    @ManyToOne(cascade = CascadeType.ALL)
    private Season season;
}
