package com.xtra.api.repository;

import com.xtra.api.model.collection.CollectionStream;
import com.xtra.api.model.collection.CollectionStreamId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionStreamRepository extends JpaRepository<CollectionStream, CollectionStreamId> {
    int countAllByIdCollectionId(Long collectionId);
}
