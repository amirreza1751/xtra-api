package com.xtra.api.repository;

import com.xtra.api.model.CollectionStream;
import com.xtra.api.model.CollectionStreamId;
import com.xtra.api.model.CollectionVod;
import com.xtra.api.model.CollectionVodId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionVodRepository extends JpaRepository<CollectionVod, CollectionVodId> {
    int countAllByIdCollectionId(Long collectionId);
}
