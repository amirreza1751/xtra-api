package com.xtra.api.mapper.admin;

import com.xtra.api.model.Resource;
import com.xtra.api.model.Server;
import com.xtra.api.projection.admin.server.ServerView;
import com.xtra.api.projection.admin.server.SimpleServerView;
import com.xtra.api.projection.admin.server.resource.ResourceView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServerMapper {
    Server convertToEntity(ServerView serverView);

    SimpleServerView convertToSimpleView(Server server);

    ServerView convertToView(Server byIdOrFail);

    ResourceView convertResourceToView(Resource resource);
}