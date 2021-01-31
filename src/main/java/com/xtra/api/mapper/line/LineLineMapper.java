package com.xtra.api.mapper.line;

import com.xtra.api.model.Line;
import com.xtra.api.projection.line.line.LineView;
//import com.xtra.api.projection.line.line.LineCreateView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class LineLineMapper {

    public abstract LineView convertToView(Line line);

//    public abstract Line convertToEntity(LineCreateView lineView);

}
