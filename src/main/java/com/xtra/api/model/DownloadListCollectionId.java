package com.xtra.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class DownloadListCollectionId implements Serializable {
    private Long downloadListId;
    private Long collectionId;
}
