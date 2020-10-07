package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class DownloadListCollectionId implements Serializable {
    private Long downloadListId;
    private Long collectionId;

    public DownloadListCollectionId() {

    }
}
