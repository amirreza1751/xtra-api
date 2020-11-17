package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class DownloadListCollection {

    @EmbeddedId
    private DownloadListCollectionId id;

    @ManyToOne
    @MapsId("collectionId")
    @JsonBackReference("dlc")
    @ToString.Exclude
    private Collection collection;

    @ManyToOne
    @MapsId("downloadListId")
    @JsonBackReference("dl")
    @ToString.Exclude
    private DownloadList downloadList;

    @Column(name = "`order`")
    private int order;

    public DownloadListCollection() {
    }

    public DownloadListCollection(Long downloadListId, Long collectionId) {
        this.id = new DownloadListCollectionId(downloadListId, collectionId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadListCollection that = (DownloadListCollection) o;
        return order == that.order &&
                Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}


