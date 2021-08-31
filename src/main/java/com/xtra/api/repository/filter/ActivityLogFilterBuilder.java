package com.xtra.api.repository.filter;

import com.querydsl.core.types.Predicate;
import com.xtra.api.model.line.QActivityLog;
import com.xtra.api.util.OptionalBooleanBuilder;


public class ActivityLogFilterBuilder {
    private final QActivityLog LOG = QActivityLog.activityLog;

    public Predicate build(ActivityLogFilter filter) {
        var filterPred = new OptionalBooleanBuilder(LOG.isNotNull())
                .notNullAnd(LOG.lineUsername::containsIgnoreCase, filter.getLineUsername())
                .notNullAnd(LOG.serverName::containsIgnoreCase, filter.getServerName())
                .notNullAnd(LOG.streamName::containsIgnoreCase, filter.getLineUsername())
                .notNullAnd(LOG.start::after, filter.getDateFrom())
                .notNullAnd(LOG.stop::before, filter.getDateTo())
                .build();
        if (filter.getSearch() == null)
            return filterPred;
        return filterPred.andAnyOf(
                LOG.streamName.containsIgnoreCase(filter.getSearch()),
                LOG.serverName.containsIgnoreCase(filter.getSearch()),
                LOG.lineUsername.containsIgnoreCase(filter.getSearch()),
                LOG.ip.containsIgnoreCase(filter.getSearch()));
    }
}