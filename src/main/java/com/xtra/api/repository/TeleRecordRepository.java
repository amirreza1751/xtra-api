package com.xtra.api.repository;

import com.xtra.api.model.vod.TeleRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeleRecordRepository extends JpaRepository<TeleRecord, Long> {
}
