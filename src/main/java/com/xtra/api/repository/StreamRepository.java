package com.xtra.api.repository;

import com.xtra.api.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreamRepository extends JpaRepository<Stream, Long> {
    Optional<Stream> getByStreamToken(String streamToken);
}
