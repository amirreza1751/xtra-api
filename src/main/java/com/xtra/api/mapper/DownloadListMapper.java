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
public abstract class DownloadListMapper {

    public abstract DownloadList convertToEntity(DownloadListDto downloadListDto);

    @Mapping(source = "collections", target = "collectionsAssign")
    public abstract DownloadList convertToEntityWithRelations(DownloadListDto downloadListDto);

    public abstract List<DownloadListCollection> convertRelationListToEntity(List<DlCollectionDto> dlCollectionDtos);

    @Mapping(target = "id.collectionId", source = "dlCollectionDto.id")
    abstract DownloadListCollection convertRelationToEntity(DlCollectionDto dlCollectionDto);

    @AfterMapping
    public void afterConvertToEntity(final DownloadListDto downloadListDto, @MappingTarget final DownloadList downloadList) {
        if (downloadList.getCollectionsAssign().isEmpty())
            return;
        var res = downloadList.getCollectionsAssign().stream().peek(dlc -> {
            var id = dlc.getId();
            id.setDownloadListId(downloadListDto.getId());
            dlc.setId(id);
        }).collect(Collectors.toList());
        downloadList.setCollectionsAssign(res);
    }

    /*To DTO */
    @Mapping(source = "collectionsAssign", target = "collections")
    public abstract DownloadListDto convertToDto(DownloadList downloadList);

    public abstract List<DlCollectionDto> convertAllToDto(List<DownloadListCollection> downloadListCollections);

    @Mapping(source = "dlCollection.collection.id", target = "id")
    @Mapping(source = "dlCollection.collection.name", target = "name")
    abstract DlCollectionDto convertToEntity(DownloadListCollection dlCollection);
    
}
