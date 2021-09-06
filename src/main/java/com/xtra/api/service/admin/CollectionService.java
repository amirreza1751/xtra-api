package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.CollectionMapper;
import com.xtra.api.model.collection.Collection;
import com.xtra.api.projection.admin.collection.CollectionInsertView;
import com.xtra.api.projection.admin.collection.CollectionSimpleView;
import com.xtra.api.projection.admin.collection.CollectionView;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.xtra.api.util.Utilities.wrapSearchString;

@Service
@Validated
public class CollectionService extends CrudService<Collection, Long, CollectionRepository> {
    private final CollectionMapper mapper;

    @Autowired
    protected CollectionService(CollectionRepository repository, CollectionMapper mapper) {
        super(repository, "Collection");
        this.mapper = mapper;
    }

    public Page<CollectionSimpleView> listCollectionsSimple(int pageNo, int pageSize, String sortBy, String sortDir) {
        return repository.listAllCollectionsSimpleView(getSortingPageable(pageNo, pageSize, sortBy, sortDir));
    }

    @Override
    protected Page<Collection> findWithSearch(String search, Pageable page) {
        search = wrapSearchString(search);
        return repository.findAllByNameLike(search, page);
    }

    public CollectionView findViewById(Long id) {
        return mapper.convertToDto(findByIdOrFail(id));
    }

    public CollectionView add(CollectionInsertView insertView) {
        return mapper.convertToDto(insert(mapper.convertToEntity(insertView)));
    }

    public CollectionView save(Long id, CollectionInsertView collection) {
        return mapper.convertToDto(repository.save(mapper.convertToEntity(collection, findByIdOrFail(id))));
    }


}
