package com.xtra.api.repository;

import com.xtra.api.model.epg.EpgFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpgFileRepository extends JpaRepository<EpgFile, Long> {
}
