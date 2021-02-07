package com.xtra.api.repository;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DownloadListRepository extends JpaRepository<DownloadList, Long> {

    List<DownloadList> findAllByOwnerUsername(String username);

    Optional<DownloadList> findByIdAndOwner(Long id, Reseller owner);

    boolean existsByIdAndOwnerUsername(Long id, String username);

    boolean existsByIdAndOwner(Long id, Reseller currentReseller);

    List<DownloadList> findAllByOwner(Reseller currentReseller);
}
