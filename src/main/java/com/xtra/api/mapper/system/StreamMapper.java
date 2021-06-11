package com.xtra.api.mapper.system;

import com.xtra.api.model.stream.StreamDetails;
import com.xtra.api.projection.system.StreamDetailsView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class StreamMapper {

    public abstract StreamDetails convertToEntity(StreamDetailsView message);
}
