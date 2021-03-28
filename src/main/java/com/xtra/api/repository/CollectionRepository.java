package com.xtra.api.repository;

import com.xtra.api.model.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    boolean existsAllByIdIn(Set<Long> ids);

    Page<Collection> findAllByNameLike(String name, Pageable pageable);
}
