package com.xtra.api.repository;

import com.xtra.api.model.stream.Channel;
import com.xtra.api.model.user.CreditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ChannelRepository extends StreamBaseRepository<Channel>, QuerydslPredicateExecutor<Channel> {

    boolean existsChannelByStreamToken(String streamToken);

    Page<Channel> findByNameLikeOrNotesLike(String name, String notes, Pageable pageable);

    List<Channel> findByIdIn(List<Long> ids);
}
