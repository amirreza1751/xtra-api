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
public class CollectionVodId implements Serializable {
    private Long collectionId;
    private Long vodId;

    public CollectionVodId(Long collectionId, Long vodId) {
        this.collectionId = collectionId;
        this.vodId = vodId;
    }

    public CollectionVodId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionVodId that = (CollectionVodId) o;
        return collectionId.equals(that.collectionId) &&
                vodId.equals(that.vodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, vodId);
    }
}
