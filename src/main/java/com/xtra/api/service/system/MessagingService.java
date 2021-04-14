package com.xtra.api.service.system;

import com.xtra.api.projection.system.StreamDetailsView;
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
    public void listen(@Header(value = "server_address", required = false) String serverAddress,
                       @Header(value = "server_port", required = false) String serverPort,
                       List<StreamDetailsView> statuses) {
        if (serverAddress == null || serverPort == null) {
            System.out.println("server identity invalid");
            return;
        }
        streamService.updateStreamStatuses(serverAddress, serverPort, statuses);
    }
}
