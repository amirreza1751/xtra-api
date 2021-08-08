package com.xtra.api.service;

import com.xtra.api.mapper.admin.LogMapper;
import com.xtra.api.model.user.*;
import com.xtra.api.projection.admin.log.CreditLogView;
import com.xtra.api.repository.CreditLogRepository;
import com.xtra.api.repository.filter.CreditLogFilter;
import com.xtra.api.util.OptionalBooleanBuilder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@Log4j2
public class CreditLogService extends CrudService<CreditLog, Long, CreditLogRepository> {

    private final QCreditLog creditLog = QCreditLog.creditLog;
    private final LogMapper logMapper;

    protected CreditLogService(CreditLogRepository repository, LogMapper logMapper) {
        super(repository, "CreditLog");
        this.logMapper = logMapper;
    }

    public Page<CreditLogView> getCreditLogs(int pageNo, int pageSize, String sortBy, String sortDir, CreditLogFilter filter) {
        var predicate = new OptionalBooleanBuilder(creditLog.isNotNull())
                .notNullAnd(creditLog.actorUsername::contains, filter.getActorUsername())
                .notNullAnd(creditLog.actorType::eq, filter.getActorUserType())
                .notNullAnd(creditLog.targetUsername::contains, filter.getTargetUsername())
                .notNullAnd(creditLog.changeAmount::goe, filter.getChangeAmountGTE())
                .notNullAnd(creditLog.changeAmount::loe, filter.getChangeAmountLTE())
                .notNullAnd(creditLog.date::after, filter.getDateFrom())
                .notNullAnd(creditLog.date::before, filter.getDateTo())
                .notNullAnd(creditLog.reason::eq, filter.getReason())
                .build();
        var search = filter.getSearch();
        if (search != null) {
            predicate = predicate.andAnyOf(
                    creditLog.actorUsername.containsIgnoreCase(search),
                    creditLog.targetUsername.containsIgnoreCase(search),
                    creditLog.description.containsIgnoreCase(search)
            );
        }

        return repository.findAll(predicate, getSortingPageable(pageNo, pageSize, sortBy, sortDir)).map(logMapper::convertToCreditLogView);
    }

    @Override
    protected Page<CreditLog> findWithSearch(String search, Pageable page) {
        return null;
    }

    public CreditLog saveCreditChangeLog(User actor, Reseller target, int initialCredits, int changeAmount, CreditLogReason reason, String description) {
        var finalCredits = initialCredits + changeAmount;
        CreditLog log = new CreditLog(actor.getUsername(), actor.getUserType(), target.getUsername(), initialCredits, finalCredits, changeAmount, LocalDateTime.now()
                , reason, description);
        repository.save(log);

        return log;
    }

    public ByteArrayResource downloadCreditLogsAsCsv(LocalDateTime dateFrom, LocalDateTime dateTo) {
        var predicate = new OptionalBooleanBuilder(creditLog.isNotNull())
                .notNullAnd(creditLog.date::before, dateTo)
                .notNullAnd(creditLog.date::after, dateFrom)
                .build();
        var logs = repository.findAll(predicate);
        StringWriter writer = new StringWriter();
        try (CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.DEFAULT.withHeader("Actor", "Actor Type", "Target", "Initial Credits", "Final Credits", "Change Amount", "Date", "Reason", "Description"))) {
            for (var log : logs) {
                printer.printRecord(log.getActorUsername(), log.getActorType(), log.getTargetUsername(),
                        log.getInitialCredits(), log.getFinalCredits(), log.getChangeAmount(), log.getDate(), log.getReason(), log.getDescription());
            }
        } catch (IOException e) {
            log.error("error in writing file");
        }
        return new ByteArrayResource(writer.toString().getBytes(StandardCharsets.UTF_8));
    }

}
