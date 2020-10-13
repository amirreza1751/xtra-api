package com.xtra.api.repository;

import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.model.DownloadListCollectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DownloadListCollectionRepository extends JpaRepository<DownloadListCollection, DownloadListCollectionId> {

    @Modifying
    @Query("delete from DownloadListCollection dlc where dlc.id.downloadListId=:downloadListId")
    void deleteAllDownloadListRelations(@Param("downloadListId") Long downloadListId);
}
