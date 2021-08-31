package com.xtra.api.repository;

import com.xtra.api.model.line.LoginLog;
import com.xtra.api.model.user.ResellerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface ResellerLogRepository extends JpaRepository<ResellerLog, Long>, QuerydslPredicateExecutor<ResellerLog> {
}
