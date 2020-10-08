package com.xtra.api.service;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.repository.DownloadListCollectionRepository;
import com.xtra.api.repository.DownloadListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class DownloadListService extends CrudService<DownloadList, Long, DownloadListRepository> {

    private final CollectionService collectionService;
    private final DownloadListCollectionRepository dlcRepository;

    protected DownloadListService(DownloadListRepository repository, CollectionService collectionService, DownloadListCollectionRepository dlcRepository) {
        super(repository, DownloadList.class);
        this.collectionService = collectionService;
        this.dlcRepository = dlcRepository;
    }

    @Override
    protected Page<DownloadList> findWithSearch(Pageable page, String search) {
        return null;
    }

    public DownloadList getDefaultDownloadList() {
        return repository.findBySystemDefaultTrue().orElseThrow(() -> new RuntimeException("default downloadList not found"));
    }

    public List<DownloadList> getDownloadListsByUserId(Long userId) {
        return repository.findAllByOwnerId(userId);
    }

    @Override
    public DownloadList updateOrFail(Long id, DownloadList downloadList) {
        dlcRepository.deleteAllByIdIn(downloadList.getCollectionsAssign().stream().map(DownloadListCollection::getId).collect(Collectors.toList()));
        var existing = findByIdOrFail(id);
        copyProperties(downloadList, existing, "id", "collectionsAssign");
        int i = 0;
        for (var collAssign : downloadList.getCollectionsAssign()) {
            var relationId = collAssign.getId();
            relationId.setDownloadListId(id);
            collAssign.setId(relationId);
            collAssign.setCollection(collectionService.findByIdOrFail(relationId.getCollectionId()));
            collAssign.setDownloadList(existing);
            collAssign.setOrder(i++);
            existing.getCollectionsAssign().add(collAssign);
        }
        return repository.save(existing);
    }

    public void saveWithRelations(DownloadList downloadList, List<DownloadListCollection> dlCollections) {
        if (downloadList == null) {
            throw new RuntimeException("downloadList is null");
        }
        AtomicInteger counter = new AtomicInteger(0);
        var relations = dlCollections.stream().peek(dlc -> {
            var id = dlc.getId();
            id.setDownloadListId(downloadList.getId());
            dlc.setId(id);
            dlc.setCollection(collectionService.findByIdOrFail(id.getCollectionId()));
            dlc.setDownloadList(downloadList);
            dlc.setOrder(counter.addAndGet(1));
        }).collect(Collectors.toList());
        downloadList.setCollectionsAssign(relations);
        repository.save(downloadList);
    }
}
