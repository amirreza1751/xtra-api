package com.xtra.api.mapper.admin;

import com.xtra.api.model.vod.Season;
import com.xtra.api.projection.admin.season.SeasonInsertView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SeasonMapper {

    public abstract Season convertToEntity(SeasonInsertView seasonInsertView);
    public abstract SeasonInsertView convertToView(Season season);

}
