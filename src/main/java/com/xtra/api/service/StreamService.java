package com.xtra.api.service;

import com.xtra.api.model.Stream;
import com.xtra.api.repository.StreamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StreamService extends CrudService<Stream, Long, StreamRepository> {

    public StreamService(StreamRepository streamRepository) {
        super(streamRepository);
    }

    public Optional<Stream> findById(Long streamId) {
        return repository.findById(streamId);
    }

    @Override
    protected Page<Stream> findWithSearch(Pageable page, String search) {
        return null;
    }
}
