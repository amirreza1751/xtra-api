package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class DownloadListCollection {

    @EmbeddedId
    private DownloadListCollectionId id;

    @ManyToOne
    @MapsId("collectionId")
    @JsonBackReference("dlc")
    @ToString.Exclude
    private Collection collection;

    @ManyToOne
    @MapsId("downloadListId")
    @JsonBackReference("dl")
    @ToString.Exclude
    private DownloadList downloadList;

    @Column(name = "`order`")
    private int order;

}


