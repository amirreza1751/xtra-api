package com.xtra.api.repository;


import com.xtra.api.model.stream.StreamInput;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StreamInputRepository extends JpaRepository<StreamInput, Long> {
    List<StreamInput> findAllByUrl(String url);
}
