package com.xtra.api.mapper.admin;

import com.xtra.api.model.collection.*;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.stream.StreamType;
import com.xtra.api.model.vod.VodType;
import com.xtra.api.projection.admin.MediaPair;
import com.xtra.api.projection.admin.collection.CollectionInsertView;
import com.xtra.api.projection.admin.collection.CollectionView;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.repository.VodRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {

    @Autowired
    private VodRepository vodRepository;
    @Autowired
    private StreamRepository streamRepository;


    /*collection for insert*/
    public abstract Collection convertToEntity(CollectionInsertView input);

    /* update existing collection*/
    @Mapping(target = "id", ignore = true)
    public abstract Collection convertToEntity(CollectionInsertView input, @MappingTarget final Collection collection);

    @AfterMapping
    public Collection addRelations(CollectionInsertView input, @MappingTarget final Collection collection) {
        collection.getVods().clear();
        addVod(input.getMovies(), collection);
        addVod(input.getSeries(), collection);
        collection.getStreams().clear();
        addStream(input.getChannels(), collection);
        addStream(input.getRadios(), collection);
        return collection;
    }

    private void addVod(List<Long> ids, final Collection collection) {
        var i = 0;
        for (var id : ids) {
            CollectionVod collectionVod = new CollectionVod(new CollectionVodId(collection.getId(), id));
            collectionVod.setOrder(i++);
            collectionVod.setCollection(collection);
            collectionVod.setVod(vodRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Vod", id)));
            collection.addVod(collectionVod);
        }
    }

    private void addStream(List<Long> ids, final Collection collection) {
        var i = 0;
        for (var id : ids) {
            CollectionStream collectionStream = new CollectionStream(new CollectionStreamId(collection.getId(), id));
            collectionStream.setOrder(i++);
            collectionStream.setCollection(collection);
            collectionStream.setStream(streamRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Stream", id)));
            collection.addStream(collectionStream);
        }
    }


    public CollectionView convertToDto(Collection collection) {
        CollectionView collectionView = new CollectionView();
        collectionView.setId(collection.getId());
        collectionView.setName(collection.getName());
        var series = emptyIfNull(collection.getVods()).stream().filter(cv -> cv.getVod().getVodType().equals(VodType.SERIES)).map(vod -> new MediaPair<>(vod.getVod().getId(), vod.getVod().getName())).collect(Collectors.toCollection(LinkedHashSet::new));
        collectionView.setSeries(series);
        var movies = emptyIfNull(collection.getVods()).stream().filter(cv -> cv.getVod().getVodType().equals(VodType.MOVIE)).map(vod -> new MediaPair<>(vod.getVod().getId(), vod.getVod().getName())).collect(Collectors.toCollection(LinkedHashSet::new));
        collectionView.setMovies(movies);
        var radios = emptyIfNull(collection.getStreams()).stream().filter(cs -> cs.getStream().getStreamType().equals(StreamType.RADIO)).map(stream -> new MediaPair<>(stream.getStream().getId(), stream.getStream().getName())).collect(Collectors.toCollection(LinkedHashSet::new));
        collectionView.setRadios(radios);
        var channels = emptyIfNull(collection.getStreams()).stream().filter(cs -> cs.getStream().getStreamType().equals(StreamType.CHANNEL)).map(stream -> new MediaPair<>(stream.getStream().getId(), stream.getStream().getName())).collect(Collectors.toCollection(LinkedHashSet::new));
        collectionView.setChannels(channels);

        return collectionView;
    }

}
