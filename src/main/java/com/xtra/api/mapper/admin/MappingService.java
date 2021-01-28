package com.xtra.api.mapper.admin;

import org.springframework.data.domain.Page;

public interface MappingService<Entity, View, SimpleView> {
    Page<SimpleView> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir);

    View getById(Long id);

    View add(View view);

    View save(Long id, View view);
}
