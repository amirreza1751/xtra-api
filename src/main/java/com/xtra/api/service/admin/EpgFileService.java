package com.xtra.api.service.admin;


import com.xtra.api.mapper.admin.EpgMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.epg.EpgInsertView;
import com.xtra.api.projection.admin.epg.EpgSimpleView;
import com.xtra.api.projection.admin.epg.EpgView;
import com.xtra.api.repository.*;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EpgFileService extends CrudService<EpgFile, Long, EpgFileRepository> {

    private final EpgMapper epgMapper;

    @Autowired
    protected EpgFileService(EpgFileRepository epgFileRepository, EpgChannelRepository epgChannelRepository, ChannelRepository channelRepository, EpgMapper epgMapper) {
        super(epgFileRepository, EpgFile.class);
        this.epgMapper = epgMapper;
    }

    @Override
    protected Page<EpgFile> findWithSearch(Pageable page, String search) {
        return null;
    }

    public Page<EpgSimpleView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(epgMapper::toSimpleView).collect(Collectors.toList()));
    }

    public EpgView updateEpgFile(Long id, EpgInsertView insertView) {
        return epgMapper.toView(updateOrFail(id, epgMapper.toEntity(insertView)));
    }

    public EpgView add(EpgInsertView insertView) {
        return epgMapper.toView(repository.save(epgMapper.toEntity(insertView)));
    }


    public EpgView getById(Long id) {
        return epgMapper.toView(findByIdOrFail(id));
    }
}
