package com.xtra.api.repository;

import com.xtra.api.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamRepository extends JpaRepository<Stream, Long> {
}
