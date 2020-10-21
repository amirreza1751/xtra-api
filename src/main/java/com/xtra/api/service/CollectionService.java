package com.xtra.api.service;

import com.google.common.collect.Sets;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.mapper.CollectionMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.CollectionInsertDto;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.CollectionStreamRepository;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.repository.VodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class CollectionService extends CrudService<Collection, Long, CollectionRepository> {
    private final CollectionMapper mapper;
    private final StreamService<Stream, StreamRepository<Stream>> streamService;
    private final VodService<Vod, VodRepository> vodService;
    private final CollectionStreamRepository csRepository;

    @Autowired
    protected CollectionService(CollectionRepository repository, CollectionMapper mapper, StreamService streamService, VodService vodService, CollectionStreamRepository csRepository) {
        super(repository, Collection.class);
        this.mapper = mapper;
        this.streamService = streamService;
        this.vodService = vodService;
        this.csRepository = csRepository;
    }

    @Override
    protected Page<Collection> findWithSearch(Pageable page, String search) {
        return null;
    }

    @Override
    public Collection insert(Collection collection) {
        collection.setStreams(collection.getStreams().stream().map(collectionStream -> {
            collectionStream.setStream(streamService.findByIdOrFail(collectionStream.getId().getStreamId()));
            return collectionStream;
        }).collect(Collectors.toSet()));

        //todo vod
        return super.insert(collection);
    }

    public Collection updateOrFail(Long id, CollectionInsertDto collection) {
        collection.setId(id);
        var newColl = mapper.convertToEntity(collection);
        var oldColl = findByIdOrFail(id);
        copyProperties(oldColl, newColl, "downloadListCollections", "streams", "vods");
        if (newColl.getStreams() != null) {
            var obsoleteStreams = Sets.difference(oldColl.getStreams(), newColl.getStreams()).immutableCopy();
            oldColl.removeStreams(obsoleteStreams);
            var unchangedStreams = oldColl.getStreams();

            oldColl.getStreams().clear();
            for (var stream : newColl.getStreams()) {
                if (!unchangedStreams.contains(stream)) {
                    CollectionStream cs = new CollectionStream(stream.getId());
                    cs.setCollection(oldColl);
                    cs.setStream(streamService.findByIdOrFail(stream.getId().getStreamId()));
                    cs.setOrder(stream.getOrder());
                    oldColl.addStream(cs);
                } else {
                    for (var cs : unchangedStreams) {
                        if (cs.getId().equals(stream.getId())) {
                            cs.setOrder(stream.getOrder());
                            oldColl.addStream(cs);
                        }
                    }
                }
            }
        }

        if (newColl.getVods() != null) {
            var obsoleteVods = Sets.difference(oldColl.getVods(), newColl.getVods()).immutableCopy();
            oldColl.removeVods(obsoleteVods);
            var newVods = Sets.difference(newColl.getVods(), oldColl.getVods()).immutableCopy();
            for (var vod : newVods) {
                oldColl.addVod(vodService.findByIdOrFail(vod.getId()));
            }
        }

        return repository.save(oldColl);
    }

    private Optional<CollectionStream> findRelationById(CollectionStreamId id) {
        return csRepository.findById(id);
    }
}
