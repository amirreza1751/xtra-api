package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    //@JsonManagedReference("channels")
    @ManyToMany(cascade = {CascadeType.MERGE})
    private Set<Stream> streams;

    //@JsonManagedReference("series")
    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<Vod> vods;

}
