package com.xtra.api.service;

import com.google.common.collect.Sets;
import com.xtra.api.mapper.admin.DownloadListMapper;
import com.xtra.api.model.download_list.DownloadList;
import com.xtra.api.projection.admin.downloadlist.DownloadListInsertView;
import com.xtra.api.projection.admin.downloadlist.DownloadListView;
import com.xtra.api.repository.DownloadListRepository;
import com.xtra.api.service.admin.CollectionService;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toSet;

public abstract class DownloadListService extends CrudService<DownloadList, Long, DownloadListRepository> {
    protected final CollectionService collectionService;
    protected final DownloadListMapper mapper;

    protected DownloadListService(DownloadListRepository repository, CollectionService collectionService, DownloadListMapper mapper) {
        super(repository, "DownloadList");
        this.collectionService = collectionService;
        this.mapper = mapper;
    }

    public DownloadListView getViewById(Long id) {
        return mapper.convertToView(super.findByIdOrFail(id));
    }


    public Page<DownloadListView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return super.findAll(search, pageNo, pageSize, sortBy, sortDir).map(mapper::convertToView);
    }

    @Transactional
    public DownloadListView updateOrFail(Long id, DownloadListInsertView downloadListView) {
        DownloadList downloadList = mapper.convertToEntityWithRelations(downloadListView);
        return mapper.convertToView(updateDownloadList(id, downloadList));
    }

    public DownloadList updateDownloadList(Long id, DownloadList downloadList) {
        var existing = findByIdOrFail(id);
        existing.setName(downloadList.getName());

        var obsoleteRelations = Sets.difference(existing.getCollectionsAssign(), downloadList.getCollectionsAssign()).immutableCopy();
        existing.getCollectionsAssign().removeAll(obsoleteRelations);

        var newRelations = Sets.difference(downloadList.getCollectionsAssign(), existing.getCollectionsAssign()).immutableCopy();
        for (var dlc : newRelations) {
            dlc.setDownloadList(existing);
            dlc.setCollection(collectionService.findByIdOrFail(dlc.getId().getCollectionId()));
            existing.getCollectionsAssign().add(dlc);
        }
        return repository.save(existing);
    }


    public DownloadListView save(DownloadListInsertView downloadListView) {
        DownloadList downloadList = mapper.convertToEntityWithRelations(downloadListView);
        return mapper.convertToView(addDownloadList(downloadList));
    }

    public DownloadList addDownloadList(DownloadList downloadList){
        downloadList.setCollectionsAssign(downloadList.getCollectionsAssign().stream().peek(dlc -> {
            dlc.setCollection(collectionService.findByIdOrFail(dlc.getId().getCollectionId()));
            dlc.setDownloadList(downloadList);
        }).collect(toSet()));
        return super.insert(downloadList);
    }
}
