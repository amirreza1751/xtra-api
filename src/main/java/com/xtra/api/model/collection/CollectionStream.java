package com.xtra.api.model.collection;

import com.xtra.api.model.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CollectionStream {
    @EmbeddedId
    @EqualsAndHashCode.Include
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
        id = new CollectionStreamId();
    }

}
