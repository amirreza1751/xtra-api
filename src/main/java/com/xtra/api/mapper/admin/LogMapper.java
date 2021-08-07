package com.xtra.api.mapper.admin;

import com.xtra.api.model.line.ActivityLog;
import com.xtra.api.model.line.Connection;
import com.xtra.api.projection.admin.log.ActivityLogView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class LogMapper {

    public abstract ActivityLogView convertToActivityLogView(ActivityLog activityLog);

    @Mapping(source = "line.username", target = "lineUsername")
    @Mapping(source = "stream.name", target = "streamName")
    @Mapping(source = "server.name", target = "serverName")
    @Mapping(source = "userIp", target = "ip")
    @Mapping(source = "startDate", target = "start")
    @Mapping(source = "lastRead", target = "stop")
    @Mapping(source = "userAgent", target = "player")
    @Mapping(target = "id", ignore = true)
    public abstract ActivityLog convertConnectionToActivityLog(Connection connection);
}
