package com.xtra.api.service;

import com.xtra.api.model.Stream;
import com.xtra.api.repository.StreamRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StreamService {
    final private StreamRepository streamRepository;

    public StreamService(StreamRepository streamRepository) {
        this.streamRepository = streamRepository;
    }

    public Optional<Stream> findById(Long streamId) {
        return streamRepository.findById(streamId);
    }
}
