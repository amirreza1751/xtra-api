package com.xtra.api.repository.filter;

import com.querydsl.core.types.Predicate;
import com.xtra.api.model.user.QResellerLog;
import com.xtra.api.util.OptionalBooleanBuilder;


public class ResellerLogFilterBuilder {
    private final QResellerLog LOG = QResellerLog.resellerLog;

    public Predicate build(ResellerLogFilter filter) {
        var filterPred = new OptionalBooleanBuilder(LOG.isNotNull())
                .notNullAnd(LOG.resellerUsername::contains, filter.getResellerUsername())
                .notNullAnd(LOG.action::eq, filter.getAction())
                .notNullAnd(LOG.date::after, filter.getDateFrom())
                .notNullAnd(LOG.date::before, filter.getDateTo())
                .build();
        if (filter.getSearch() == null)
            return filterPred;
        return filterPred.andAnyOf(
                LOG.resellerUsername.containsIgnoreCase(filter.getSearch()));
    }
}