package com.xtra.api.repository.filter;

import com.querydsl.core.types.Predicate;
import com.xtra.api.model.line.QLoginLog;
import com.xtra.api.util.OptionalBooleanBuilder;


public class LoginLogFilterBuilder {
    private final QLoginLog LOG = QLoginLog.loginLog;

    public Predicate build(LoginLogFilter filter) {
        var filterPred = new OptionalBooleanBuilder(LOG.isNotNull())
                .notNullAnd(LOG.user.id::eq, filter.getUserId())
                .notNullAnd(LOG.date::after, filter.getDateFrom())
                .notNullAnd(LOG.date::before, filter.getDateTo())
                .build();
        if (filter.getSearch() == null)
            return filterPred;
        return filterPred.andAnyOf(
                LOG.user.username.containsIgnoreCase(filter.getSearch()),
                LOG.ip.containsIgnoreCase(filter.getSearch()));
    }
}