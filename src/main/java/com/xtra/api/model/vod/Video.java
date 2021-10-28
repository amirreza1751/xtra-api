package com.xtra.api.model.vod;

import com.xtra.api.model.server.Server;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    private String token;

    @Enumerated(EnumType.STRING)
    private EncodeStatus encodeStatus = EncodeStatus.NOT_ENCODED;

    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<Resolution> targetResolutions;

    @ManyToOne
    private Server sourceServer;

    private String sourceLocation;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private VideoInfo sourceVideoInfo;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<VideoInfo> targetVideosInfos;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Subtitle> sourceSubtitles = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Audio> sourceAudios = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<VideoServer> videoServers;
}
