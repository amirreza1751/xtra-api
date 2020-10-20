package com.xtra.api.mapper;

import com.xtra.api.model.Package;
import com.xtra.api.projection.PackageView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DownloadListMapper.class)
public interface PackageMapper {

    PackageView convertToDto(Package pack);
}
