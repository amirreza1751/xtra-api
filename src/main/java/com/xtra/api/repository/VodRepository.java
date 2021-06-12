package com.xtra.api.repository;

import com.xtra.api.model.vod.Vod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VodRepository extends JpaRepository<Vod, Long> {
}
