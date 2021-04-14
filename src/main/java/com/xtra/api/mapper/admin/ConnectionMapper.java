package com.xtra.api.mapper.admin;

import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import com.xtra.api.projection.admin.ConnectionIdView;
import com.xtra.api.projection.admin.ConnectionView;
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
    @Mapping(source = "id", target = "connectionIdView")
    public abstract ConnectionView convertToView(LineActivity lineActivity);


    @AfterMapping
    void durationCalculation(final LineActivity lineActivity, @MappingTarget final ConnectionView connectionView){
        connectionView.setDuration(Duration.between(lineActivity.getStartDate(), lineActivity.getLastRead()).toMillis());
    }

    public abstract ConnectionIdView convertToView(LineActivityId lineActivityId);

    public abstract LineActivityId convertToEntity(ConnectionIdView connectionIdView);

}
