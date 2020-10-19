package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class CollectionStreamId implements Serializable {
    private Long collectionId;
    private Long streamId;

    public CollectionStreamId(Long collectionId, Long streamId) {
        this.collectionId = collectionId;
        this.streamId = streamId;
    }

    public CollectionStreamId() {
    }
}
