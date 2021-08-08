package com.xtra.api.service.admin;

import com.xtra.api.model.event.SettingChangedEvent;
import com.xtra.api.model.setting.BackupInterval;
import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.service.ProcessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Service
public class BackupService implements SchedulingConfigurer {
    private final ProcessService processService;
    private ScheduledTaskRegistrar scheduledTaskRegistrar;
    private final TaskScheduler dynamicTaskScheduler;
    private ScheduledFuture<?> scheduledBackup;
    private BackupInterval backupInterval = BackupInterval.NONE;

    @Value("${spring.datasource.username}")
    String dbUsername;

    @Value("${spring.datasource.password}")
    String dbPassword;

    @Value("${spring.datasource.url}")
    String dbUrl;

    public BackupService(ProcessService processService, TaskScheduler dynamicTaskScheduler) {
        this.processService = processService;
        this.dynamicTaskScheduler = dynamicTaskScheduler;
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
        path += "backup-";
        if (manual)
            path += "manual";
        else
            path += "automatic";
        path += "_" + LocalDateTime.now() + ".sql";
        String cmd = String.format("/usr/bin/mysqldump -u%s -p%s --add-drop-table %s -r %s",
                dbUsername, dbPassword, dbAndTables.toString().trim(), path);
        processService.runProcess(cmd.split(" "));
    }

}
