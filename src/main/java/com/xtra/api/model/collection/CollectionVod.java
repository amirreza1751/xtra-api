package com.xtra.api.model.collection;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xtra.api.model.vod.Vod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"collection"})
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
