package com.xtra.api.mapper.admin;

import com.xtra.api.model.vod.TeleRecord;
import com.xtra.api.projection.admin.catchup.TeleRecordListView;
import com.xtra.api.repository.LineRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


@Mapper(componentModel = "spring")
public abstract class TeleRecordMapper {

    @Autowired
    private LineRepository lineRepository;

    @Value("${server.external.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    public abstract TeleRecordListView convertToListView(TeleRecord teleRecord);

    @AfterMapping
    void convertTeleRecordInfo(final TeleRecord teleRecord, @MappingTarget final TeleRecordListView teleRecordListView) {
        var system_line = lineRepository.findByUsername("system_line");
        String link = system_line.map(line -> "http://" + serverAddress + ":" + serverPort + "/api/play/video/" + line.getLineToken() + "/" + teleRecord.getVideo().getToken()).orElse("");

        teleRecordListView.setChannelName(teleRecord.getChannel().getName());
        teleRecordListView.setServerName(teleRecord.getVideo().getVideoServers().stream().findFirst().get().getServer().getName());
        teleRecordListView.setStart(teleRecord.getStart().toString());
        teleRecordListView.setStop(teleRecord.getStop().toString());
        teleRecordListView.setDuration(teleRecord.getDuration().toString());
        teleRecordListView.setLink(link);
    }

}
