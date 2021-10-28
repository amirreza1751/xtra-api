package com.xtra.api.mapper.admin;

import com.xtra.api.model.line.VodConnection;
import com.xtra.api.projection.admin.connection.ConnectionView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Duration;

@Mapper(componentModel = "spring")
public abstract class VodConnectionMapper {

    @Mapping(source = "line.username", target = "lineUsername")
    @Mapping(source = "server.name", target = "serverName")
    @Mapping(source = "userIp", target = "ip")
    public abstract ConnectionView convertToView(VodConnection vodConnection);

    @AfterMapping
    void durationCalculation(final VodConnection vodConnection, @MappingTarget final ConnectionView vodConnectionView) {
        vodConnectionView.setDuration(Duration.between(vodConnection.getStartDate(), vodConnection.getLastRead()).toSeconds());
        String[] location = vodConnection.getVideo().getSourceLocation().split("/");
        vodConnectionView.setStreamName(location[location.length - 1]);
    }

}
