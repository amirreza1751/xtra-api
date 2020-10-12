package com.xtra.api.repository;


import com.xtra.api.model.StreamServer;
import com.xtra.api.model.StreamServerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamServerRepository extends JpaRepository<StreamServer, StreamServerId> {
}
