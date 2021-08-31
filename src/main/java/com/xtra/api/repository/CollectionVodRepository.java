package com.xtra.api.repository;

import com.xtra.api.model.collection.CollectionVod;
import com.xtra.api.model.collection.CollectionVodId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionVodRepository extends JpaRepository<CollectionVod, CollectionVodId> {
    int countAllByIdCollectionId(Long collectionId);
}
