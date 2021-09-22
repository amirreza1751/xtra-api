package com.xtra.api.mapper.admin;

import com.xtra.api.model.vod.TeleRecord;
import com.xtra.api.projection.admin.catchup.TeleRecordListView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public abstract class TeleRecordMapper {

    public abstract TeleRecordListView convertToListView(TeleRecord teleRecord);

    @AfterMapping
    void convertTeleRecordInfo(final TeleRecord teleRecord, @MappingTarget final TeleRecordListView teleRecordListView) {
        teleRecordListView.setChannelName(teleRecord.getChannel().getName());
        teleRecordListView.setServerName(teleRecord.getVideo().getVideoServers().stream().findFirst().get().getServer().getName());
        teleRecordListView.setStart(teleRecord.getStart().toString());
        teleRecordListView.setStop(teleRecord.getStop().toString());
        teleRecordListView.setDuration(teleRecord.getDuration().toString());
    }

}
