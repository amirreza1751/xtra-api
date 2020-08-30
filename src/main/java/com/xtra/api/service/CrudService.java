package com.xtra.api.service;

import com.xtra.api.exceptions.EntityNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;

import static com.xtra.api.util.Utilities.getSortingPageable;
import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class CrudService<T, ID, Repository extends JpaRepository<T, ID>> {
    protected final Repository repository;
    protected final Class<T> aClass;

    protected CrudService(Repository repository, Class<T> aClass) {
        this.repository = repository;
        this.aClass = aClass;
    }

    public T getByIdOrFail(ID id) {
        var result = repository.findById(id);
        return result.orElseThrow(() -> new EntityNotFoundException(aClass.getName(), id.toString()));
    }

    protected abstract Page<T> findWithSearch(Pageable page, String search);

    public Page<T> getAll(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        var page = getSortingPageable(pageNo, pageSize, sortBy, sortDir);
        if (StringUtils.isEmpty(search))
            return repository.findAll(page);
        else
            return findWithSearch(page, search);
    }

    public T add(T object) {
        return repository.save(object);
    }

    public T updateOrFail(ID id, T newObject) {
        var result = repository.findById(id);
        T oldObject = result.orElseThrow(() -> new EntityNotFoundException(aClass.getName(), id.toString()));
        copyProperties(newObject, oldObject, "id");
        return repository.save(oldObject);
    }

    public void delete(ID id) {
        if (!repository.existsById(id))
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
        repository.deleteById(id);
    }


}
