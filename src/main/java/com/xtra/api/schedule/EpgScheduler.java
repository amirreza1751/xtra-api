package com.xtra.api.schedule;

import org.springframework.scheduling.annotation.Scheduled;

public class EpgScheduler {

    @Scheduled(cron = "0 0 * * 1")
    public void updateEpg(){

    }
}
