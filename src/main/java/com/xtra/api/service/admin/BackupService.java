package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.BackupMapper;
import com.xtra.api.model.Backup;
import com.xtra.api.model.ProcessHandler;
import com.xtra.api.model.event.SettingChangedEvent;
import com.xtra.api.model.setting.BackupInterval;
import com.xtra.api.projection.BackupView;
import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.repository.BackupRepository;
import com.xtra.api.service.CrudService;
import com.xtra.api.service.ProcessService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Service
@Log4j2
public class BackupService extends CrudService<Backup, Long, BackupRepository> implements SchedulingConfigurer {
    private final ProcessService processService;
    private ScheduledTaskRegistrar scheduledTaskRegistrar;
    private final TaskScheduler dynamicTaskScheduler;
    private ScheduledFuture<?> scheduledBackup;
    private BackupInterval backupInterval = BackupInterval.NONE;
    private final BackupMapper backupMapper;

    @Value("${spring.datasource.username}")
    String dbUsername;

    @Value("${spring.datasource.password}")
    String dbPassword;

    @Value("${spring.datasource.url}")
    String dbUrl;

    public BackupService(BackupRepository backupRepository, ProcessService processService, TaskScheduler dynamicTaskScheduler, BackupMapper backupMapper) {
        super(backupRepository, "Backup");
        this.processService = processService;
        this.dynamicTaskScheduler = dynamicTaskScheduler;
        this.backupMapper = backupMapper;
    }

    @Override
    public synchronized void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (this.scheduledTaskRegistrar == null) {
            this.scheduledTaskRegistrar = taskRegistrar;
        }
        this.scheduledTaskRegistrar.setScheduler(dynamicTaskScheduler);
        CronTrigger trigger;
        switch (backupInterval) {
            case HOURLY:
                trigger = new CronTrigger("0 0 * * * *");
                break;
            case DAILY:
                trigger = new CronTrigger("0 0 0 * * *");
                break;
            case WEEKLY:
                trigger = new CronTrigger("0 0 0 * * 0");
                break;
            case MONTHLY:
                trigger = new CronTrigger("0 0 0 1 * *");
                break;
            default:
                return;
        }
        scheduledBackup = Objects.requireNonNull(this.scheduledTaskRegistrar
                .getScheduler())
                .schedule(() -> {
                    System.out.println("task running at " + LocalDateTime.now());
                }, trigger);
    }

    @EventListener()
    public void updateSchedule(SettingChangedEvent event) {
        if (scheduledBackup != null)
            this.scheduledBackup.cancel(false);
        backupInterval = BackupInterval.fromString(((SettingView) event.getSource()).getValue());
        configureTasks(this.scheduledTaskRegistrar);
    }

    public void createBackup(boolean manual, List<String> tables) {
        String dbName = StringUtils.substringAfterLast(dbUrl, '/');
        StringBuilder dbAndTables = new StringBuilder(dbName + " ");
        if (tables != null && tables.size() > 0) {
            for (var table : tables) {
                dbAndTables.append(table).append(" ");
            }
        }
        var path = System.getProperty("user.home") + File.separator + "backups" + File.separator;
        var name = "backup-";
        if (manual)
            name += "manual";
        else
            name += "automatic";
        name += "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")) + ".sql";
        path += name;
        String cmd = String.format("/usr/bin/mysqldump -u%s -p%s --add-drop-table %s -r %s",
                dbUsername, dbPassword, dbAndTables.toString().trim(), path);
        String fileName = name;
        String finalPath = path;
        processService.runProcess(cmd.split(" "), new ProcessHandler(() -> {
            long size;
            try {
                size = Files.size(Paths.get(finalPath));
                var backup = new Backup(fileName, size, LocalDateTime.now());
                repository.save(backup);
            } catch (IOException e) {
                log.error("could not get file size of file with path: " + finalPath + File.separator + fileName);
            }

        }));
    }

    public Page<BackupView> getBackupList(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return findAll(search, pageNo, pageSize, sortBy, sortDir).map(backupMapper::convertToView);
    }

    @Override
    protected Page<Backup> findWithSearch(String search, Pageable page) {
        return null;
    }

    public void restoreBackup(Long id) {
        var backup = findByIdOrFail(id);
        var path = System.getProperty("user.home") + File.separator + "backups" + File.separator;
//        String cmd = String.format("/usr/bin/mysql -u%s -p%s --add-drop-table %s -r %s",
//                dbUsername, dbPassword, dbAndTables.toString().trim(), path + File.separator + backup.getFileName());
    }
}
