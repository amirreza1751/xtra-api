package com.xtra.api.service.system;

import com.xtra.api.service.admin.ChannelService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {
    private final ChannelService channelService;

    public ScheduleService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Scheduled(fixedDelay = 10000)
    public void test(){
        channelService.autoStopOnDemandChannels();
    }
}
