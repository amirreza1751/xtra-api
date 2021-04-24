package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CollectionVod {
    @EmbeddedId
    @EqualsAndHashCode.Include
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
        id = new CollectionVodId();
    }
}
