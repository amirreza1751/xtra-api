package com.xtra.api.repository;

import com.xtra.api.model.stream.Channel;
import com.xtra.api.model.vod.TeleRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeleRecordRepository extends JpaRepository<TeleRecord, Long> {
    Page<TeleRecord> findAllByChannel(Channel channel, Pageable page);
}
