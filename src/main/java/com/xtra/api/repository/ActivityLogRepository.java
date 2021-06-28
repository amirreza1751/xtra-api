package com.xtra.api.repository;

import com.xtra.api.model.line.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>, QuerydslPredicateExecutor<ActivityLog> {
}
