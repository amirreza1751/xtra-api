package com.xtra.api.repository.filter;

import com.querydsl.core.types.Predicate;
import com.xtra.api.model.line.QActivityLog;
import com.xtra.api.util.OptionalBooleanBuilder;


public class ActivityLogFilterBuilder {
    private final QActivityLog LOG = QActivityLog.activityLog;

    public Predicate build(ActivityLogFilter filter) {
        return new OptionalBooleanBuilder(LOG.isNotNull())
                .notNullAnd(LOG.stream.id::eq,filter.getStreamId())
                .notNullAnd(LOG.server.id::eq,filter.getServerId())
                .notNullAnd(LOG.line.id::eq,filter.getLineId())
                .notNullAnd(LOG.start::after, filter.getDateFrom())
                .notNullAnd(LOG.stop::before, filter.getDateTo())
                .build();
    }
}