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

    @Mapping(source = "line.id", target = "lineId")
    @Mapping(source = "stream.id", target = "streamId")
    @Mapping(source = "server.id", target = "serverId")
    @Mapping(source = "line.username", target = "lineUsername")
    @Mapping(source = "stream.name", target = "streamName")
    @Mapping(source = "server.name", target = "serverName")
    public abstract ActivityLogView convertToActivityLogView(ActivityLog activityLog);

    @Mapping(source = "userIp", target = "ip")
    @Mapping(source = "startDate", target = "start")
    @Mapping(source = "lastRead", target = "stop")
    @Mapping(source = "userAgent", target = "player")
    @Mapping(target = "id", ignore = true)
    public abstract ActivityLog convertConnectionToActivityLog(Connection connection);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    public abstract LoginLogView convertToLoginLogView(LoginLog loginLog);

    @Mapping(source = "reseller.id", target = "resellerId")
    @Mapping(source = "reseller.username", target = "resellerUsername")
    @Mapping(source = "line.id", target = "lineId")
    @Mapping(source = "lie.username", target = "lineUsername")
    public abstract ResellerLogView convertToResellerLogView(ResellerLog resellerLog);

    @Mapping(source = "actor.id",target = "actorId")
    @Mapping(source = "actor.username",target = "actorUsername")
    @Mapping(source = "actor.userType",target = "actorUserType")
    @Mapping(source = "target.id",target = "targetId")
    @Mapping(source = "target.username",target = "targetUsername")
    @Mapping(source = "reason.text",target = "reason")
    public abstract CreditLogView convertToCreditLogView(CreditLog creditLog);
}
