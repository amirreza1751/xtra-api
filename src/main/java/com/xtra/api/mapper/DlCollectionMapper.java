package com.xtra.api.mapper;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.projection.DlCollectionDto;
import com.xtra.api.projection.DownloadListDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class DlCollectionMapper {


    public abstract List<DownloadListCollection>  convertAllToEntity(List<DlCollectionDto> dlCollectionDtos);


    @Mapping(target = "id.collectionId", source = "dlCollectionDto.id")
    abstract DownloadListCollection convertToDto(DlCollectionDto dlCollectionDto);

    @AfterMapping
    public void afterConvertToEntity(final DownloadListDto downloadListDto, @MappingTarget final DownloadList downloadList) {
        var res = downloadList.getCollectionsAssign().stream().peek(dlc -> {
            var id = dlc.getId();
            id.setDownloadListId(downloadListDto.getId());
            dlc.setId(id);
        }).collect(Collectors.toList());
        downloadList.setCollectionsAssign(res);
    }


}
