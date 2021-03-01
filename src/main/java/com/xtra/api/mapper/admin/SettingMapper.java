package com.xtra.api.mapper.admin;

import com.xtra.api.model.Setting;
import com.xtra.api.projection.admin.SettingView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SettingMapper {
    @Mapping(target = "key", source = "id")
    SettingView toView(Setting setting);
}
