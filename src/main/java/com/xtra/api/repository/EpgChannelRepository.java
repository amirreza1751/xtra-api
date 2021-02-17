package com.xtra.api.repository;

import com.xtra.api.model.EpgChannel;
import com.xtra.api.model.EpgChannelId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EpgChannelRepository extends JpaRepository<EpgChannel, EpgChannelId> {
}
