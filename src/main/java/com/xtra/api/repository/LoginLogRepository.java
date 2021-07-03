package com.xtra.api.repository;

import com.xtra.api.model.line.ActivityLog;
import com.xtra.api.model.line.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface LoginLogRepository extends JpaRepository<LoginLog, Long>, QuerydslPredicateExecutor<LoginLog> {
}
