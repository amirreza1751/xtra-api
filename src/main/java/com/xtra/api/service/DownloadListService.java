package com.xtra.api.service;

import com.google.common.collect.Sets;
import com.xtra.api.mapper.admin.DownloadListMapper;
import com.xtra.api.model.DownloadList;
import com.xtra.api.projection.admin.downloadlist.DownloadListInsertView;
import com.xtra.api.projection.admin.downloadlist.DownloadListView;
import com.xtra.api.repository.DownloadListRepository;
import com.xtra.api.service.admin.CollectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public abstract class DownloadListService extends CrudService<DownloadList, Long, DownloadListRepository> {
    protected final CollectionService collectionService;
    protected final DownloadListMapper mapper;

    protected DownloadListService(DownloadListRepository repository, CollectionService collectionService, DownloadListMapper mapper) {
        super(repository, DownloadList.class);
        this.collectionService = collectionService;
        this.mapper = mapper;
    }

    public DownloadListView getViewById(Long id) {
        return mapper.convertToView(super.findByIdOrFail(id));
    }


    public Page<DownloadListView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var result = super.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return new PageImpl<>(result.stream().map(mapper::convertToView).collect(Collectors.toList()));
    }

    @Transactional
    public DownloadListView updateOrFail(Long id, DownloadListInsertView downloadListView) {
        downloadListView.setId(id);
        DownloadList downloadList = mapper.convertToEntityWithRelations(downloadListView);
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
        return mapper.convertToView(repository.save(existing));
    }


    public DownloadListView save(DownloadListInsertView downloadListView) {
        DownloadList downloadList = mapper.convertToEntityWithRelations(downloadListView);
        downloadList.setCollectionsAssign(downloadList.getCollectionsAssign().stream().peek(dlc -> {
            dlc.setCollection(collectionService.findByIdOrFail(dlc.getId().getCollectionId()));
            dlc.setDownloadList(downloadList);
        }).collect(toSet()));
        return mapper.convertToView(super.insert(downloadList));
    }
}
