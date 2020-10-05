package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class DownloadListCollection {

    @EmbeddedId
    private DownloadListCollectionId id;

    @ManyToOne
    @MapsId("collectionId")
    @JsonBackReference("collection")
    private Collection collection;

    @ManyToOne
    @MapsId("downloadListId")
    @JsonBackReference("dl")
    private DownloadList downloadList;

    private boolean isEnabled;

    @Data
    @Embeddable
    public class DownloadListCollectionId implements Serializable {
        private Long downloadListId;
        private Long collectionId;

        public DownloadListCollectionId() {

        }
    }
}


