package com.xtra.api.mapper.admin;

import com.xtra.api.model.line.Connection;
import com.xtra.api.projection.admin.connection.ConnectionView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Duration;

@Mapper(componentModel = "spring")
public abstract class ConnectionMapper {

    @Mapping(source = "line.username", target = "lineUsername")
    @Mapping(source = "stream.name", target = "streamName")
    @Mapping(source = "server.name", target = "serverName")
    @Mapping(source = "userIp", target = "ip")
    public abstract ConnectionView convertToView(Connection connection);

    @AfterMapping
    void durationCalculation(final Connection connection, @MappingTarget final ConnectionView connectionView) {
        connectionView.setDuration(Duration.between(connection.getStartDate(), connection.getLastRead()).toSeconds());
    }

}
