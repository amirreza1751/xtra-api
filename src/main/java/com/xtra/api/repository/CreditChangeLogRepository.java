package com.xtra.api.repository;

import com.xtra.api.model.CreditChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditChangeLogRepository extends JpaRepository<CreditChangeLog, Long> {
}