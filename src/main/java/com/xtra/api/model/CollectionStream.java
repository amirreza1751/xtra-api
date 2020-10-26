package com.xtra.api.model;

import com.google.common.base.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
public class CollectionStream {
    @EmbeddedId
    private CollectionStreamId id;

    @MapsId("streamId")
    @ManyToOne
    private Stream stream;

    @MapsId("collectionId")
    @ManyToOne
    private Collection collection;

    @Column(name = "`order`")
    private int order;

    public CollectionStream(CollectionStreamId id) {
        this.id = id;
    }

    public CollectionStream() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionStream that = (CollectionStream) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
