package com.xtra.api.repository;


import com.xtra.api.model.StreamInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StreamInfoRepository extends JpaRepository<StreamInfo, Long> {
    //Optional<StreamInfo> findByStreamId(Long streamId);
}
