package com.xtra.api.mapper.admin;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.downloadlist.DlCollectionView;
import com.xtra.api.projection.line.LineInsertView;
import com.xtra.api.projection.line.LineView;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.service.admin.UserService;
import org.apache.commons.lang3.RandomUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Mapper(componentModel = "spring", uses = DownloadListMapper.class)
public abstract class LineMapper {

    @Autowired
    private UserService userService;
    @Autowired
    private CollectionRepository collectionRepository;

    @Mapping(source = "collections", target = "defaultDownloadList")
    public abstract Line convertToEntity(LineInsertView insertView);

    Reseller convertFromId(Long ownerId) {
        if (ownerId == null)
            return null;
        return (Reseller) userService.findByIdOrFail(ownerId);
    }

    DownloadList convertCollectionIdsToDownloadList(LinkedHashSet<Long> collectionIds) {
        if (collectionIds == null || collectionIds.size() == 0)
            return null;
        DownloadList dl = new DownloadList();
        dl.setName("generated_" + RandomUtils.nextInt(1000000, 9999999));
        int order = 0;
        var dlcList = new HashSet<DownloadListCollection>();
        for (var collectionId : collectionIds) {
            DownloadListCollection dlc = new DownloadListCollection(null, collectionId);
            dlc.setOrder(order++);
            dlc.setCollection(collectionRepository.findById(collectionId).orElseThrow(() -> new EntityNotFoundException("Collection", collectionId.toString())));
            dlc.setDownloadList(dl);
            dlcList.add(dlc);
        }
        dl.setCollectionsAssign(dlcList);
        return dl;
    }

    @Mapping(source = "defaultDownloadList", target = "collections")
    public abstract LineView convertToView(Line line);

    List<DlCollectionView> convertDownloadListToDlCollectionView(DownloadList downloadList) {
        if (downloadList==null)
            return null;
        return emptyIfNull(downloadList.getCollectionsAssign()).stream().map(dlc -> new DlCollectionView(dlc.getCollection().getId(), dlc.getCollection().getName())).collect(Collectors.toList());
    }
}
