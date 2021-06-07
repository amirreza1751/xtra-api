package com.xtra.api.service.system;

import com.xtra.api.model.Connection;
import com.xtra.api.projection.system.ConnectionDetails;
import com.xtra.api.projection.system.StreamDetailsView;
import com.xtra.api.service.admin.ConnectionService;
import com.xtra.api.service.admin.StreamService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessagingService {
    private final StreamService streamService;

    public MessagingService(StreamService streamService) {
        this.streamService = streamService;
    }

    @RabbitListener(queues = "streamStatus")
    public void listenForStreamUpdates(@Header(value = "token", required = false) String token,
                                       List<StreamDetailsView> statuses) {
        if (token == null) {
            System.out.println("server identity invalid");
            return;
        }
        streamService.updateStreamStatuses(token, statuses);
    }
}
