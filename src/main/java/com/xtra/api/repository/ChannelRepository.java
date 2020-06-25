package com.xtra.api.repository;

import com.xtra.api.model.Channel;
import com.xtra.api.model.Server;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChannelRepository extends JpaRepository<Channel, Long>, JpaSpecificationExecutor<Channel> {
    Page<Channel> findByNameOrCategoryNameOrCurrentInputUrlOrServersContains(String name, String categoryName,String currentInputUrl, Server server, Pageable pageable);
    Page<Channel> findByNameOrCategoryNameOrCurrentInputUrl(String name, String categoryName,String currentInputUrl, Pageable pageable);

}
