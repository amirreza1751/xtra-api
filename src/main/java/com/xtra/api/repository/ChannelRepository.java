package com.xtra.api.repository;

import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import com.xtra.api.model.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ChannelRepository extends StreamRepository<Channel> {

    boolean existsChannelByStreamToken(String streamToken);

    Page<Channel> findByNameLikeOrCategoryNameLikeOrStreamInfoCurrentInputLike(String name, String categoryName, String currentInputUrl, Pageable pageable);

}
