package com.xtra.api.repository;

import com.xtra.api.model.user.CreditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CreditLogRepository extends JpaRepository<CreditLog, Long>, QuerydslPredicateExecutor<CreditLog> {
}
