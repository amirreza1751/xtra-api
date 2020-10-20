package com.xtra.api.service;

import com.google.common.collect.Sets;
import com.xtra.api.mapper.DownloadListMapper;
import com.xtra.api.model.DownloadList;
import com.xtra.api.projection.DownloadListView;
import com.xtra.api.repository.DownloadListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Service
public class DownloadListService extends CrudService<DownloadList, Long, DownloadListRepository> {

    private final CollectionService collectionService;
    private final DownloadListMapper mapper;

    protected DownloadListService(DownloadListRepository repository, CollectionService collectionService, DownloadListMapper mapper) {
        super(repository, DownloadList.class);
        this.collectionService = collectionService;
        this.mapper = mapper;
    }

    @Override
    protected Page<DownloadList> findWithSearch(Pageable page, String search) {
        return null;
    }


    public List<DownloadList> getDownloadListsByUserId(Long userId) {
        return repository.findAllByOwnerId(userId);
    }

    public DownloadListView getViewById(Long id) {
        return mapper.convertToDto(super.findByIdOrFail(id));
    }


    public Page<DownloadListView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var result = super.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return new PageImpl<>(result.stream().map(mapper::convertToDto).collect(Collectors.toList()));
    }

    @Transactional
    public DownloadListView updateOrFail(Long id, DownloadListView downloadListView) {
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
        return mapper.convertToDto(repository.save(existing));
    }


    public DownloadListView save(DownloadListView downloadListView) {
        DownloadList downloadList = mapper.convertToEntityWithRelations(downloadListView);
        downloadList.setCollectionsAssign(downloadList.getCollectionsAssign().stream().peek(dlc -> {
            dlc.setCollection(collectionService.findByIdOrFail(dlc.getId().getCollectionId()));
            dlc.setDownloadList(downloadList);
        }).collect(toSet()));
        return mapper.convertToDto(super.insert(downloadList));
    }

}
