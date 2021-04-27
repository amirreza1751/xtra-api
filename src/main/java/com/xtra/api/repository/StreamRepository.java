package com.xtra.api.repository;

import com.xtra.api.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface StreamRepository<T extends Stream> extends JpaRepository<T, Long> {
    Optional<T> getByToken(String streamToken);

    boolean existsByToken(String streamToken);

    boolean existsByName(String name);
}
