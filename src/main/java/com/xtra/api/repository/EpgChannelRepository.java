package com.xtra.api.repository;

import com.xtra.api.model.EpgChannel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpgChannelRepository extends JpaRepository<EpgChannel, Long> {
}
