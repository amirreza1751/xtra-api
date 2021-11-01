package com.xtra.api.repository;

import com.xtra.api.model.stream.Channel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface ChannelRepository extends StreamBaseRepository<Channel>, QuerydslPredicateExecutor<Channel> {

    boolean existsChannelByStreamToken(String streamToken);

    Page<Channel> findByNameLikeOrNotesLike(String name, String notes, Pageable pageable);

    List<Channel> findByIdIn(List<Long> ids);

    @Query(nativeQuery = true, value = "SELECT stream.*, COUNT(*) as count FROM `connection` LEFT JOIN stream ON connection.stream_id=stream.id GROUP BY stream.id ORDER BY count DESC LIMIT 10 ")
    List<Channel> Top10Channels();
}
