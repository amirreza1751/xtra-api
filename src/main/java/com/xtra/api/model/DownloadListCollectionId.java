package com.xtra.api.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Setter
@Embeddable
public class DownloadListCollectionId implements Serializable {
    private Long downloadListId;
    private Long collectionId;

    public DownloadListCollectionId() {

    }
}
