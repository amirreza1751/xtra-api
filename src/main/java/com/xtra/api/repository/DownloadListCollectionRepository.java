package com.xtra.api.repository;

import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.model.DownloadListCollectionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DownloadListCollectionRepository extends JpaRepository<DownloadListCollection, Long> {
    void deleteAllByIdIn(List<DownloadListCollectionId> ids);
}
