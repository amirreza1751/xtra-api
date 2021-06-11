package com.xtra.api.mapper.line;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.download_list.DownloadList;
import com.xtra.api.model.download_list.DownloadListCollection;
import com.xtra.api.model.line.Line;
import com.xtra.api.projection.line.LineSecurityView;
import com.xtra.api.projection.line.line.LineInsertView;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.repository.CollectionRepository;
import org.apache.commons.lang3.RandomUtils;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;

//import com.xtra.api.projection.line.line.LineCreateView;

@Mapper(componentModel = "spring")
public abstract class LineLineMapper {
    @Autowired
    private CollectionRepository collectionRepository;

    public abstract LineView convertToView(Line line);

    public abstract Line convertToEntity(LineInsertView lineView);

    public DownloadList convertCollectionIdsToDownloadList(Long downloadListId, List<Long> collectionIds) {
        if (collectionIds == null || collectionIds.size() == 0)
            return null;
        DownloadList dl = new DownloadList();
        dl.setName("generated_" + RandomUtils.nextInt(1000000, 9999999));
        int order = 0;
        var dlcList = new HashSet<DownloadListCollection>();
        for (var collectionId : collectionIds) {
            DownloadListCollection dlc = new DownloadListCollection(downloadListId, collectionId);
            dlc.setOrder(order++);
            dlc.setCollection(collectionRepository.findById(collectionId).orElseThrow(() -> new EntityNotFoundException("Collection", collectionId.toString())));
            dlc.setDownloadList(dl);
            dlcList.add(dlc);
        }
        dl.setCollectionsAssign(dlcList);
        return dl;
    }

    public abstract LineSecurityView getSecurityDetails(Line currentLine);
}
