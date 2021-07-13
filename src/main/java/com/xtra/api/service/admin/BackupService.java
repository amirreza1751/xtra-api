package com.xtra.api.service.admin;

import com.xtra.api.service.ProcessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BackupService {
    private final ProcessService processService;

    @Value("${spring.datasource.username}")
    String dbUsername;

    @Value("${spring.datasource.password}")
    String dbPassword;

    @Value("${spring.datasource.url}")
    String dbUrl;

    public BackupService(ProcessService processService) {
        this.processService = processService;
    }

    public void createBackup(boolean manual, List<String> tables) {
        String dbName = StringUtils.substringAfterLast(dbUrl, '/');
        StringBuilder dbAndTables = new StringBuilder(dbName + " ");
        if (tables.size() > 0) {
            for (var table : tables) {
                dbAndTables.append(table).append(" ");
            }
        }
        var path = System.getProperty("user.home") + File.separator + "backups" + File.separator;
        path += "backup-";
        if (manual)
            path += "manual";
        else
            path += "scheduled";
        path += "_" + LocalDateTime.now() + ".sql";
        String cmd = String.format("/usr/bin/mysqldump -u%s -p%s --add-drop-table %s -r %s",
                dbUsername, dbPassword, dbAndTables.toString().trim(), path);
        processService.runProcess(cmd.split(" "));
    }
}
