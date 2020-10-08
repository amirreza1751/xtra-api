package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private Collection collection;

    @ManyToOne
    @MapsId("downloadListId")
    @JsonBackReference("dl")
    private DownloadList downloadList;

    private boolean isEnabled;
    @Column(name = "`order`")
    private int order;
    
}


