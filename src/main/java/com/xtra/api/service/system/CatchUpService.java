package com.xtra.api.service.system;

import com.xtra.api.projection.admin.catchup.CatchupRecordView;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service("systemCatchUpService")
public class CatchUpService {
    private final ServerService serverService;

    public CatchUpService(ServerService serverService) {
        this.serverService = serverService;
    }

    public void updateRecordingStatus(@Header(value = "token", required = false) String token,
                                       boolean status,
                                      Long streamId,
                                      CatchupRecordView catchupRecordView) {
        if (token == null) {
            System.out.println("server identity invalid");
            return;
        }
        serverService.updateRecordingStatus(token, status, streamId, catchupRecordView);
    }

}
