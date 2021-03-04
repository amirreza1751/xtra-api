package com.xtra.api.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString(exclude = {"downloadListCollections","streams","vods"})
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.REMOVE)
    private Set<DownloadListCollection> downloadListCollections;

    @ManyToOne(optional = false)
    private Category category;

    @OrderBy("order ASC")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "collection", orphanRemoval = true)
    private Set<CollectionStream> streams;

    @OrderBy("order ASC")
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "collection", orphanRemoval = true)
    private Set<CollectionVod> vods;

    public void addStream(CollectionStream stream) {
        if (streams == null) streams = new HashSet<>();
        streams.add(stream);
    }

    public void removeStream(CollectionStream stream) {
        if (streams == null) return;
        streams.remove(stream);
    }

    public void removeStreams(java.util.Collection<CollectionStream> streams) {
        if (streams == null) return;
        this.streams.removeAll(streams);
    }

    public void addVod(CollectionVod vod) {
        if (vods == null) vods = new HashSet<>();
        vods.add(vod);
    }

    public void removeVod(CollectionVod vod) {
        if (vods == null) return;
        vods.remove(vod);
    }

    public void removeVods(java.util.Collection<CollectionVod> vods) {
        if (vods == null) return;
        this.vods.removeAll(vods);
    }


}
