package com.xtra.api.repository.filter;

import com.querydsl.core.types.Predicate;
import com.xtra.api.model.line.QActivityLog;
import com.xtra.api.util.OptionalBooleanBuilder;


public class ActivityLogFilterBuilder {
    private final QActivityLog LOG = QActivityLog.activityLog;

    public Predicate build(ActivityLogFilter filter) {
        var filterPred = new OptionalBooleanBuilder(LOG.isNotNull())
                .notNullAnd(LOG.stream.id::eq, filter.getStreamId())
                .notNullAnd(LOG.server.id::eq, filter.getServerId())
                .notNullAnd(LOG.line.id::eq, filter.getLineId())
                .notNullAnd(LOG.start::after, filter.getDateFrom())
                .notNullAnd(LOG.stop::before, filter.getDateTo())
                .build();
        if (filter.getSearch() == null)
            return filterPred;
        return filterPred.andAnyOf(LOG.line.username.containsIgnoreCase(filter.getSearch()),
                LOG.stream.name.containsIgnoreCase(filter.getSearch()),
                LOG.server.name.containsIgnoreCase(filter.getSearch()),
                LOG.ip.containsIgnoreCase(filter.getSearch()));
    }
}