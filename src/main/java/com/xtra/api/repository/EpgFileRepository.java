package com.xtra.api.repository;

import com.xtra.api.model.EpgFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpgFileRepository extends JpaRepository<EpgFile, Long> {
}
