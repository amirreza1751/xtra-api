package com.xtra.api.mapper;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.projection.DlCollectionDto;
import com.xtra.api.projection.DownloadListDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DownloadListMapper {

    public abstract DownloadList convertToEntity(DownloadListDto downloadListDto);

    @Mapping(target = "collections",source = "collectionsAssign")
    public abstract DownloadListDto convertToDto(DownloadList downloadList);

    public abstract List<DlCollectionDto> convertAllToDto(List<DownloadListCollection> downloadListCollections);

    @Mapping(target = "id", source = "dlCollection.collection.id")
    @Mapping(target = "name", source = "dlCollection.collection.name")
    abstract DlCollectionDto convertToEntity(DownloadListCollection dlCollection);

}
