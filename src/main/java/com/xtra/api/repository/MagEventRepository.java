package com.xtra.api.repository;

import com.xtra.api.model.mag.MagEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MagEventRepository extends JpaRepository<MagEvent, Long> {
    long countAllByMagDeviceIdAndStatus(long id, int status);

    Optional<MagEvent> findFirstByMagDeviceIdAndStatusOrderById(long id, int status);
}
