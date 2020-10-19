package com.xtra.api.repository;

import com.xtra.api.model.CollectionStream;
import com.xtra.api.model.CollectionStreamId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionStreamRepository extends JpaRepository<CollectionStream, CollectionStreamId> {
}
