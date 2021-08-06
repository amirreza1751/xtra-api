package com.xtra.api.repository;

import com.xtra.api.model.mag.MagDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MagRepository extends JpaRepository<MagDevice, Long> {
    Optional<MagDevice> findByMac(String mac);

    Optional<MagDevice> findByToken(String token);

}
