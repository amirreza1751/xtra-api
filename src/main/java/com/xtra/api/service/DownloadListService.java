package com.xtra.api.service;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.repository.DownloadListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class DownloadListService extends CrudService<DownloadList, Long, DownloadListRepository> {

    private final CollectionService collectionService;

    protected DownloadListService(DownloadListRepository repository, CollectionService collectionService) {
        super(repository, DownloadList.class);
        this.collectionService = collectionService;
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

    public void saveRelationship(DownloadList downloadList, List<DownloadListCollection> dlCollections) {
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
