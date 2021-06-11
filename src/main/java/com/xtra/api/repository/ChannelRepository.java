package com.xtra.api.repository;

import com.xtra.api.model.stream.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChannelRepository extends StreamRepository<Channel> {

    boolean existsChannelByStreamToken(String streamToken);

    Page<Channel> findByNameLikeOrNotesLike(String name, String notes, Pageable pageable);

    List<Channel> findByIdIn(List<Long> ids);
}
