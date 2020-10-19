package com.xtra.api.mapper;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.model.DownloadListCollectionId;
import com.xtra.api.projection.DlCollectionDto;
import com.xtra.api.projection.DownloadListView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class DownloadListMapper {

    public abstract DownloadList convertToEntity(DownloadListView downloadListView);

    @Mapping(source = "collections", target = "collectionsAssign")
    public abstract DownloadList convertToEntityWithRelations(DownloadListView downloadListView);

    public Set<DownloadListCollection> convertRelationListToEntity(List<DlCollectionDto> dlCollectionDtos) {
        if (dlCollectionDtos == null)
            return null;
        Set<DownloadListCollection> dlcSet = new LinkedHashSet<>();
        int i = 0;
        for (var dlcDto : dlCollectionDtos) {
            var dlc = new DownloadListCollection();
            dlc.setId(new DownloadListCollectionId(null, dlcDto.getId()));
            dlc.setOrder(i++);
            dlcSet.add(dlc);
        }
        return dlcSet;
    }

    @AfterMapping
    public void afterConvertToEntity(final DownloadListView downloadListView, @MappingTarget final DownloadList downloadList) {
        if (downloadList.getCollectionsAssign() == null)
            return;
        var res = downloadList.getCollectionsAssign().stream().peek(dlc -> {
            var id = dlc.getId();
            id.setDownloadListId(downloadListView.getId());
            dlc.setId(id);
        }).collect(Collectors.toSet());
        downloadList.setCollectionsAssign(res);
    }

    /*To DTO */
    @Mapping(source = "collectionsAssign", target = "collections")
    public abstract DownloadListView convertToDto(DownloadList downloadList);

    public List<DlCollectionDto> convertAllToDto(Set<DownloadListCollection> downloadListCollections) {
        if (downloadListCollections == null)
            return new ArrayList<>();
        
        List<DownloadListCollection> dlcList = new ArrayList<>(downloadListCollections);
        dlcList.sort(Comparator.comparingInt(DownloadListCollection::getOrder));

        List<DlCollectionDto> dlcDtoList = new ArrayList<>();
        for (var dlc : dlcList) {
            dlcDtoList.add(new DlCollectionDto(dlc.getId().getCollectionId(), dlc.getCollection().getName()));
        }
        return dlcDtoList;
    }

    @Mapping(source = "dlCollection.collection.id", target = "id")
    @Mapping(source = "dlCollection.collection.name", target = "name")
    abstract DlCollectionDto convertToEntity(DownloadListCollection dlCollection);

}
