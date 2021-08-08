package com.xtra.api.mapper.admin;

import com.xtra.api.model.line.ActivityLog;
import com.xtra.api.model.line.Connection;
import com.xtra.api.model.line.LoginLog;
import com.xtra.api.model.user.CreditLog;
import com.xtra.api.model.user.ResellerLog;
import com.xtra.api.projection.admin.log.ActivityLogView;
import com.xtra.api.projection.admin.log.CreditLogView;
import com.xtra.api.projection.admin.log.LoginLogView;
import com.xtra.api.projection.admin.log.ResellerLogView;
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

    public abstract LoginLogView convertToLoginLogView(LoginLog loginLog);

    @Mapping(source = "reseller.id", target = "resellerId")
    @Mapping(source = "reseller.username", target = "resellerUsername")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "userUsername")
    public abstract ResellerLogView convertToResellerLogView(ResellerLog resellerLog);

    @Mapping(source = "reason.text",target = "reason")
    public abstract CreditLogView convertToCreditLogView(CreditLog creditLog);
}
