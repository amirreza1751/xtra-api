package com.xtra.api.service;


import com.xtra.api.model.*;
import com.xtra.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class EpgFileService extends CrudService<EpgFile, Long, EpgFileRepository> {

    private final EpgChannelRepository epgChannelRepository;
    private final ChannelRepository channelRepository;
    @Autowired
    protected EpgFileService(EpgFileRepository epgFileRepository, EpgChannelRepository epgChannelRepository, ChannelRepository channelRepository){
        super(epgFileRepository, EpgFile.class);
        this.epgChannelRepository = epgChannelRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    protected Page<EpgFile> findWithSearch(Pageable page, String search) {
        return null;
    }


    public EpgFile updateEpgFile(Long id, EpgFile epgFile) {
        return updateOrFail(id, epgFile);
    }

    public EpgFile add(EpgFile epgFile){
        Set<EpgChannel> epgChannels = new HashSet<>(epgFile.getEpgChannels());
        epgChannels.forEach(epgChannel -> epgChannel.setEpgFile(epgFile));
        return repository.save(epgFile);
    }

    public Channel addEpgChannelToStream(Long epgChannelId, Long streamId){
        EpgChannel epgChannel = epgChannelRepository.findById(epgChannelId).orElse(null);
        Channel channel = channelRepository.findById(streamId).orElse(null);
        channel.setEpgChannel(epgChannel);
       return channelRepository.save(channel);
    }

    public Set<EpgChannel> getEpgChannels(Long epgFileId){
        return repository.findById(epgFileId).get().getEpgChannels();
    }

}
