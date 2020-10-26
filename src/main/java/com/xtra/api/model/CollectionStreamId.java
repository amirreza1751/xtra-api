package com.xtra.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@ToString
public class CollectionStreamId implements Serializable {
    private Long collectionId;
    private Long streamId;

    public CollectionStreamId(Long collectionId, Long streamId) {
        this.collectionId = collectionId;
        this.streamId = streamId;
    }

    public CollectionStreamId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionStreamId that = (CollectionStreamId) o;
        return Objects.equals(collectionId, that.collectionId) &&
                Objects.equals(streamId, that.streamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, streamId);
    }
}
