package com.xtra.api.service;

import com.xtra.api.model.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ServerService {
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    public boolean sendStartRequest(Long channelId) {
        var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/start/" + channelId, String.class);
        return true;
    }

    public void sendRestartRequest(Long channelId) {
        new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/restart/" + channelId, String.class);
    }

    public boolean sendStopRequest(Long channelId) {
        var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/streams/stop/" + channelId, String.class);
        return true;
    }

    public void sendKillAllConnectionRequest(Long lineId){
        var result = new RestTemplate().getForObject(corePath + ":" + corePort + "/lines/kill_connections/" + lineId, Boolean.class);
    }

    public Optional<Server> findByName(String search) {
        return null;
    }
}
