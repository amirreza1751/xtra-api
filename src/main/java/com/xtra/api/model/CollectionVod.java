package com.xtra.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
public class CollectionVod {

    @EmbeddedId
    private CollectionVodId id;

    @MapsId("vodId")
    @ManyToOne
    private Vod vod;

    @MapsId("collectionId")
    @ManyToOne
    private Collection collection;

    @Column(name = "`order`")
    private int order;

    public CollectionVod(CollectionVodId id) {
        this.id = id;
    }

    public CollectionVod() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionVod that = (CollectionVod) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
