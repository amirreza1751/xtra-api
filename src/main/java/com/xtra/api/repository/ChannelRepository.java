package com.xtra.api.repository;

import com.xtra.api.model.Channel;
import com.xtra.api.model.LineStatus;
import com.xtra.api.model.Server;
import com.xtra.api.model.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends StreamRepository<Channel> {

    boolean existsChannelByToken(String streamToken);

    Page<Channel> findByNameLikeOrNotesLike(String name, String notes, Pageable pageable);

    List<Channel> findByIdIn(List<Long> ids);

}
