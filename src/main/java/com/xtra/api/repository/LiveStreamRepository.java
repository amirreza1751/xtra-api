package com.xtra.api.repository;

import com.xtra.api.model.LiveStream;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveStreamRepository extends JpaRepository<LiveStream, Long> {
}
