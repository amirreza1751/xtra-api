package com.xtra.api.service.reseller;

import com.xtra.api.mapper.admin.DownloadListMapper;
import com.xtra.api.model.DownloadList;
import com.xtra.api.projection.admin.downloadlist.DownloadListInsertView;
import com.xtra.api.projection.admin.downloadlist.DownloadListView;
import com.xtra.api.repository.DownloadListRepository;
import com.xtra.api.service.DownloadListService;
import com.xtra.api.service.admin.CollectionService;
import com.xtra.api.service.admin.ResellerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.xtra.api.service.system.SystemResellerService.getCurrentReseller;
import static java.util.stream.Collectors.toSet;


@Service("ResellerDownloadListService")
@PreAuthorize("hasRole('RESELLER')")
public class DownloadListServiceImpl extends DownloadListService {

    protected DownloadListServiceImpl(DownloadListRepository repository, DownloadListMapper downloadListMapper, CollectionService collectionService, ResellerService resellerService) {
        super(repository, collectionService, downloadListMapper);
    }

    @Override
    protected Page<DownloadList> findWithSearch(Pageable page, String search) {
        return null;
    }

    public List<DownloadListView> getResellerDownloadLists() {
        List<DownloadList> downloadLists = repository.findAllByOwner(getCurrentReseller());
        return downloadLists.stream().map(mapper::convertToView).collect(Collectors.toList());
    }

    @Override
    public DownloadListView getViewById(Long id) {
        return mapper.convertToView(findByIdAndOwnerOrFail(id));
    }

    private DownloadList findByIdAndOwnerOrFail(Long id) {
        var currentReseller = getCurrentReseller();
        return repository.findByIdAndOwner(id, currentReseller).orElseThrow(entityNotFoundException("Reseller", currentReseller.getUsername()));
    }

    @Override
    public DownloadListView save(DownloadListInsertView downloadListView) {
        DownloadList downloadList = mapper.convertToEntityWithRelations(downloadListView);
        downloadList.setCollectionsAssign(downloadList.getCollectionsAssign().stream().peek(dlc -> {
            dlc.setCollection(collectionService.findByIdOrFail(dlc.getId().getCollectionId()));
            dlc.setDownloadList(downloadList);
        }).collect(toSet()));
        downloadList.setOwner(getCurrentReseller());
        return mapper.convertToView(super.insert(downloadList));
    }

    //@todo delete and check for existence in one query
    @Override
    public void deleteOrFail(Long id) {
        if (!repository.existsByIdAndOwner(id, getCurrentReseller()))
            entityNotFoundException("id", id);
        repository.deleteById(id);
    }
}
