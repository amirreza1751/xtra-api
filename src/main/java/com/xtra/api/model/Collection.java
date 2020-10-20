package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private CollectionType type;

    @JsonManagedReference("dlc")
    @OneToMany(mappedBy = "collection")
    private Set<DownloadListCollection> downloadListCollections;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Stream> streams;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<Vod> vods;

    public void addStream(Stream stream) {
        if (streams == null) streams = new HashSet<>();
        streams.add(stream);
    }

    public void removeStream(Stream stream) {
        if (streams == null) return;
        streams.remove(stream);
    }

    public void removeStreams(java.util.Collection<Stream> streams) {
        if (streams == null) return;
        this.streams.removeAll(streams);
    }

    public void addVod(Vod vod) {
        if (vods == null) vods = new HashSet<>();
        vods.add(vod);
    }

    public void removeVod(Vod vod) {
        if (vods == null) return;
        vods.remove(vod);
    }

    public void removeVods(java.util.Collection<Vod> vods) {
        if (vods == null) return;
        this.vods.removeAll(vods);
    }


}
