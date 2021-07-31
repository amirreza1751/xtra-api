package com.xtra.api.service.admin;


import com.xtra.api.mapper.admin.TeleRecordMapper;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.model.vod.TeleRecord;
import com.xtra.api.model.vod.Video;
import com.xtra.api.projection.admin.catchup.TeleRecordListView;
import com.xtra.api.projection.admin.series.SeriesView;
import com.xtra.api.repository.TeleRecordRepository;
import com.xtra.api.repository.VideoRepository;
import com.xtra.api.service.CrudService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Validated
public class TeleRecordService extends CrudService<TeleRecord, Long, TeleRecordRepository> {

    private final TeleRecordMapper teleRecordMapper;
    private final ChannelService channelService;

    @Autowired
    protected TeleRecordService(TeleRecordRepository teleRecordRepository, TeleRecordMapper teleRecordMapper, ChannelService channelService){
        super(teleRecordRepository, "TeleRecord");
        this.teleRecordMapper = teleRecordMapper;
        this.channelService = channelService;
    }

    @Override
    protected Page<TeleRecord> findWithSearch(String search, Pageable page) {
        return null;
    }

    public Page<TeleRecordListView> findAllByChannel(Long channelId, int pageNo, int pageSize, String sortBy, String sortDir) {
        var channel = channelService.findByIdOrFail(channelId);
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        return repository.findAllByChannel(channel, page).map(teleRecordMapper::convertToListView);
    }

}
