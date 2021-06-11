package com.xtra.api.mapper.reseller;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.download_list.DownloadList;
import com.xtra.api.model.download_list.DownloadListCollection;
import com.xtra.api.model.line.Line;
import com.xtra.api.projection.admin.downloadlist.DlCollectionView;
import com.xtra.api.projection.reseller.line.LineUpdateView;
import com.xtra.api.projection.reseller.line.LineView;
import com.xtra.api.projection.reseller.line.LineCreateView;
import com.xtra.api.repository.CollectionRepository;
import org.apache.commons.lang3.RandomUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Mapper(componentModel = "spring")
public abstract class ResellerLineMapper {

    @Autowired
    private CollectionRepository collectionRepository;

    @Mapping(source = "collections", target = "defaultDownloadList")
    public abstract Line convertToEntity(LineCreateView lineView);

    @Mapping(source = "collections", target = "defaultDownloadList")
    public abstract Line convertToEntity(LineUpdateView lineView);

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
        if (downloadList == null)
            return null;
        return emptyIfNull(downloadList.getCollectionsAssign()).stream().map(dlc -> new DlCollectionView(dlc.getCollection().getId(), dlc.getCollection().getName())).collect(Collectors.toList());
    }
}
