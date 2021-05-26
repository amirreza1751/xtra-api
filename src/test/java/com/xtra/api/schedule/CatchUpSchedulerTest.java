package com.xtra.api.schedule;

import com.xtra.api.model.Server;
import com.xtra.api.model.StreamServerId;
import com.xtra.api.projection.admin.catchup.CatchupRecordView;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.admin.ServerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class CatchUpSchedulerTest {
    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private StreamServerRepository streamServerRepository;

    @Autowired
    private ServerService serverService;

    @Test
    void checkEpgUpdate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm[XXX]");
        var t = ZonedDateTime.now().format(formatter);

        Server server = serverRepository.findById(1L).get();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss [XXX][X]");
        CatchupRecordView catchupRecordView = new CatchupRecordView();
        catchupRecordView.setTitle("testamir");
        catchupRecordView.setStreamInput("http://tivix.eu:8000/iptenjoyim/3GaALqqI2tcL/62748");
        catchupRecordView.setStart(ZonedDateTime.parse("20210513014800 +0200", formatter1));
        catchupRecordView.setStop(ZonedDateTime.parse("20210513015000 +0200", formatter1));

        CatchupRecordView catchupRecordView2 = new CatchupRecordView();
        catchupRecordView2.setTitle("testamir2");
        catchupRecordView2.setStreamInput("http://tivix.eu:8000/iptenjoyim/3GaALqqI2tcL/62748");
        catchupRecordView2.setStart(ZonedDateTime.parse("20210513015000 +0200", formatter1));
        catchupRecordView2.setStop(ZonedDateTime.parse("20210513015100 +0200", formatter1));

        var streamServer = streamServerRepository.findById(new StreamServerId(1L, server.getId()));
        if (!streamServer.get().getRecording() &&catchupRecordView.getStart().withZoneSameInstant(ZoneOffset.UTC).toString().equals(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).format(formatter))){
            Boolean res = serverService.sendRecordRequest(1L, server, catchupRecordView);
            if (res){
                streamServer.ifPresent(item -> {
                    item.setRecording(true);
                    streamServerRepository.save(item);
                });
            }
            System.out.println("request sent.1");
        }

        streamServer = streamServerRepository.findById(new StreamServerId(1L, server.getId()));
        if (!streamServer.get().getRecording() &&catchupRecordView2.getStart().withZoneSameInstant(ZoneOffset.UTC).toString().equals(ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).format(formatter))){
            Boolean res = serverService.sendRecordRequest(1L, server, catchupRecordView2);
            if (res){
                streamServer.ifPresent(item -> {
                    item.setRecording(true);
                    streamServerRepository.save(item);
                });
            }
            System.out.println("request sent.2");
        }
    }
}