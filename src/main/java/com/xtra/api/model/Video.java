package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;

    @Enumerated(EnumType.STRING)
    private EncodeStatus encodeStatus;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Subtitle> subtitles = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<Audio> audios = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private VideoInfo videoInfo;

    @OneToMany(mappedBy = "video", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<VideoServer> videoServers;
}
