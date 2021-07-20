package com.xtra.api.repository;

import com.xtra.api.model.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StreamRepository extends JpaRepository<Stream, Long> {
    List<Stream> findAllByNameContains(String name);

    Optional<Stream> findByStreamToken(String token);
}
