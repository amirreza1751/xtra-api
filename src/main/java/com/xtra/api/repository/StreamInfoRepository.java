package com.xtra.api.repository;


import com.xtra.api.model.StreamInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreamInfoRepository extends JpaRepository<StreamInfo, Long> {
}
