package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.function.Supplier;

import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class CrudService<T, ID, Repository extends JpaRepository<T, ID>> {
    protected final Repository repository;
    protected final String entityName;

    protected CrudService(Repository repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    public T findByIdOrFail(ID id) {
        var result = repository.findById(id);
        return result.orElseThrow(entityNotFoundException("id", id));
    }

    protected abstract Page<T> findWithSearch(Pageable page, String search);

    public Page<T> findAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        if (StringUtils.isEmpty(search))
            return repository.findAll(page);
        else
            return findWithSearch(page, search);
    }

    public T insert(T object) {
        return repository.save(object);
    }

    public T updateOrFail(ID id, T newObject) {
        T oldObject = findByIdOrFail(id);
        copyProperties(newObject, oldObject, "id");
        return repository.save(oldObject);
    }

    public T updateOrCreate(ID id, T newObject) {
        Optional<T> oldObject = repository.findById(id);
        if (oldObject.isEmpty()) {
            return repository.save(newObject);
        }
        copyProperties(newObject, oldObject, "id", "streamServers");
        return repository.save(oldObject.get());
    }

    public void deleteOrFail(ID id) {
        if (!repository.existsById(id))
            entityNotFoundException("id", id);
        repository.deleteById(id);
    }

    protected Pageable getSortingPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable page;
        Sort.Order order;
        if (sortBy != null && !sortBy.equals("")) {
            if (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                order = Sort.Order.desc(sortBy);
            else
                order = Sort.Order.asc(sortBy);
            page = PageRequest.of(pageNo, pageSize, Sort.by(order));
        } else {
            page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc("id")));
        }
        return page;
    }

    protected Supplier<EntityNotFoundException> entityNotFoundException(String field, Object fieldValue) throws EntityNotFoundException {
        throw new EntityNotFoundException(entityName, field, fieldValue.toString());
    }


}
