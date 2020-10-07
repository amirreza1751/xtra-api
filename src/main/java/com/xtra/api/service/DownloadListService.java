package com.xtra.api.service;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.DownloadListCollection;
import com.xtra.api.repository.DownloadListCollectionRepository;
import com.xtra.api.repository.DownloadListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DownloadListService extends CrudService<DownloadList, Long, DownloadListRepository> {

    private final DownloadListCollectionRepository dlcRepository;
    private final CollectionService collectionService;

    protected DownloadListService(DownloadListRepository repository, DownloadListCollectionRepository dlcRepository, CollectionService collectionService) {
        super(repository, DownloadList.class);
        this.dlcRepository = dlcRepository;
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
        var relations = dlCollections.stream().peek(dlc -> {
            var id = dlc.getId();
            id.setDownloadListId(downloadList.getId());
            dlc.setId(id);
            dlc.setCollection(collectionService.findByIdOrFail(id.getCollectionId()));
            dlc.setDownloadList(downloadList);
        }).collect(Collectors.toList());
        //dlcRepository.saveAll(relations);
        downloadList.setCollectionsAssign(relations);
        repository.save(downloadList);
    }
}
