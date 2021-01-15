package com.xtra.api.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@ToString(exclude = {"collection", "downloadList"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
public class DownloadListCollection {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private DownloadListCollectionId id;

    @ManyToOne
    @MapsId("collectionId")
    private Collection collection;

    @ManyToOne
    @MapsId("downloadListId")
    private DownloadList downloadList;

    @Column(name = "`order`")
    private int order;

    public DownloadListCollection(Long downloadListId, Long collectionId) {
        this.id = new DownloadListCollectionId(downloadListId, collectionId);
    }

}


