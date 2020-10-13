package com.xtra.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class DownloadListCollectionId implements Serializable {
    private Long downloadListId;
    private Long collectionId;

    public DownloadListCollectionId() {

    }

    public DownloadListCollectionId(Long downloadListId, Long collectionId) {
        this.downloadListId = downloadListId;
        this.collectionId = collectionId;
    }
}
