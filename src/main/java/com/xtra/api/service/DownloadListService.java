package com.xtra.api.service;

import com.xtra.api.model.DownloadList;
import com.xtra.api.repository.DownloadListRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownloadListService extends CrudService<DownloadList, Long, DownloadListRepository> {

    protected DownloadListService(DownloadListRepository repository) {
        super(repository, DownloadList.class);
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
}
