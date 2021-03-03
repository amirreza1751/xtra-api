package com.xtra.api.schedule;

import com.xtra.api.service.admin.EpgFileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EpgScheduler {
    private final EpgFileService epgFileService;

    public EpgScheduler(EpgFileService epgFileService) {
        this.epgFileService = epgFileService;
    }

    //@todo dynamic time
    @Scheduled(cron = "0 0 1 * * MON")
    public void checkEpgUpdate() {
        epgFileService.syncAllEpg();
    }

}
