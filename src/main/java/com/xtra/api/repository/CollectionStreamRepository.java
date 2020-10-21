package com.xtra.api.repository;

import com.xtra.api.model.CollectionStream;
import com.xtra.api.model.CollectionStreamId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CollectionStreamRepository extends JpaRepository<CollectionStream, CollectionStreamId> {
    int countAllByIdCollectionId(Long collectionId);
}
