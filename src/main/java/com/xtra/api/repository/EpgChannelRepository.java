package com.xtra.api.repository;

import com.xtra.api.model.epg.EpgChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EpgChannelRepository extends JpaRepository<EpgChannel, Long> {

    Optional<EpgChannel> findByNameAndLanguageAndEpgFile_Id(String name, String language, Long epgFileId);
}
