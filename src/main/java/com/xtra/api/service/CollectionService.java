package com.xtra.api.service;

import com.google.common.collect.Sets;
import com.xtra.api.mapper.CollectionMapper;
import com.xtra.api.model.Collection;
import com.xtra.api.model.Stream;
import com.xtra.api.model.Vod;
import com.xtra.api.projection.CollectionInsertDto;
import com.xtra.api.repository.CollectionRepository;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.repository.VodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class CollectionService extends CrudService<Collection, Long, CollectionRepository> {
    private final CollectionMapper mapper;
    private final StreamService<Stream, StreamRepository<Stream>> streamService;
    private final VodService<Vod, VodRepository> vodService;

    @Autowired
    protected CollectionService(CollectionRepository repository, CollectionMapper mapper, StreamService streamService, VodService vodService) {
        super(repository, Collection.class);
        this.mapper = mapper;
        this.streamService = streamService;
        this.vodService = vodService;
    }

    @Override
    protected Page<Collection> findWithSearch(Pageable page, String search) {
        return null;
    }

    public Collection updateOrFail(Long id, CollectionInsertDto collection) {
        collection.setId(id);
        var newColl = mapper.convertToEntity(collection);
        var oldColl = findByIdOrFail(id);
        copyProperties(oldColl, newColl, "downloadListCollections", "streams", "vods");
        if (newColl.getStreams() != null) {
            var obsoleteStreams = Sets.difference(oldColl.getStreams(), newColl.getStreams()).immutableCopy();
            oldColl.removeStreams(obsoleteStreams);
            var newStreams = Sets.difference(newColl.getStreams(), oldColl.getStreams()).immutableCopy();
            for (var stream : newStreams) {
                oldColl.addStream(streamService.findByIdOrFail(stream.getId()));
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
}
