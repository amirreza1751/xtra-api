package com.xtra.api.service.system;

import com.xtra.api.projection.system.ConnectionDetails;
import com.xtra.api.projection.system.StreamDetailsView;
import com.xtra.api.service.admin.ConnectionService;
import com.xtra.api.service.admin.StreamService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("systemCatchUpService")
public class CatchUpService {
    private final ServerService serverService;

    public CatchUpService(ServerService serverService) {
        this.serverService = serverService;
    }

    public void updateRecordingStatus(@Header(value = "token", required = false) String token,
                                       boolean status,
                                      Long streamId) {
        if (token == null) {
            System.out.println("server identity invalid");
            return;
        }
        serverService.updateRecordingStatus(token, status, streamId);
    }

}
