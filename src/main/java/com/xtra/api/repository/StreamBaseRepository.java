package com.xtra.api.repository;

import com.xtra.api.model.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreamBaseRepository<T extends Stream> extends JpaRepository<T, Long> {
    Optional<T> findByStreamToken(String streamToken);

    boolean existsByStreamToken(String streamToken);

    boolean existsByName(String name);
}
