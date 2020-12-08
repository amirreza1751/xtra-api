package com.xtra.api.mapper;

import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.line.LineInsertView;
import com.xtra.api.projection.line.LineView;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.service.UserService;
import org.apache.commons.lang3.RandomUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.LinkedHashSet;

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

    public abstract LineView convertToView(Line line);
}
