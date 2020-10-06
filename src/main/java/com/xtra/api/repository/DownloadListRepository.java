package com.xtra.api.repository;

import com.xtra.api.model.DownloadList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DownloadListRepository extends JpaRepository<DownloadList, Long> {
    Optional<DownloadList> findBySystemDefaultTrue();
}
