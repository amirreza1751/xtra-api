package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.DownloadListMapper;
import com.xtra.api.model.download_list.DownloadList;
import com.xtra.api.repository.DownloadListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DownloadListService extends com.xtra.api.service.DownloadListService {

    protected DownloadListService(DownloadListRepository repository, CollectionService collectionService, DownloadListMapper mapper) {
        super(repository, collectionService, mapper);
    }

    @Override
    protected Page<DownloadList> findWithSearch(String search, Pageable page) {
        return null;
    }


}
