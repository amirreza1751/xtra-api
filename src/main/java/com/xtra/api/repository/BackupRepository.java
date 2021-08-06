package com.xtra.api.repository;

import com.xtra.api.model.Backup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupRepository extends JpaRepository<Backup, Long> {
}
