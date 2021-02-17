package com.xtra.api.mapper.admin;

import com.xtra.api.model.EpgFile;
import com.xtra.api.projection.admin.epg.EpgInsertView;
import com.xtra.api.projection.admin.epg.EpgSimpleView;
import com.xtra.api.projection.admin.epg.EpgView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class EpgMapper {
    public abstract EpgSimpleView toSimpleView(EpgFile epgFile);

    public abstract EpgView toView(EpgFile epgFile);

    public abstract EpgFile toEntity(EpgInsertView epgInsertView);
}
