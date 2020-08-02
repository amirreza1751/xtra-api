package com.xtra.api.repository;

import com.xtra.api.model.Vod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VodRepository extends JpaRepository<Vod, Long> {
}
