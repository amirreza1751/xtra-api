package com.xtra.api.mapper.admin;

import com.xtra.api.model.epg.EpgChannel;
import com.xtra.api.model.epg.EpgFile;
import com.xtra.api.projection.admin.epg.EpgChannelView;
import com.xtra.api.projection.admin.epg.EpgInsertView;
import com.xtra.api.projection.admin.epg.EpgSimpleView;
import com.xtra.api.projection.admin.epg.EpgView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class EpgMapper {
    public abstract EpgSimpleView toSimpleView(EpgFile epgFile);

    @Mapping(target = "channels",source = "epgChannels")
    public abstract EpgView toView(EpgFile epgFile);

    public abstract EpgFile toEntity(EpgInsertView epgInsertView);

    public abstract EpgChannelView toView(EpgChannel epgChannel);
}
