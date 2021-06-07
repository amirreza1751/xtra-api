package com.xtra.api.service;

import com.xtra.api.service.admin.ConnectionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {
    private final ConnectionService connectionService;

    public ScheduleService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Scheduled(fixedDelay = 1000)
    private void connectionCleaner(){
        connectionService.deleteOldConnections();
    }
}
