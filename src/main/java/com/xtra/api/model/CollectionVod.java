package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Vod vod;

    @MapsId("collectionId")
    @ManyToOne
    @JsonBackReference
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
