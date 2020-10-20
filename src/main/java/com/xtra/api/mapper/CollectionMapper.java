package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.CollectionDto;
import com.xtra.api.projection.CollectionInsertDto;
import com.xtra.api.projection.CollectionSimplifiedDto;
import org.mapstruct.Mapper;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {

    /*collection simple display*/

    public CollectionSimplifiedDto convertToSimpleDto(Collection collection) {
        CollectionSimplifiedDto collectionSimplifiedDto = new CollectionSimplifiedDto();

        collectionSimplifiedDto.setId(collection.getId());
        collectionSimplifiedDto.setName(collection.getName());
        collectionSimplifiedDto.setType(collection.getType());

        switch (collection.getType()) {
            case SERIES:
                collectionSimplifiedDto.setSeries(collection.getVods().size());
                break;
            case MOVIE:
                collectionSimplifiedDto.setMovies(collection.getVods().size());
                break;
            case RADIO:
                collectionSimplifiedDto.setRadios(collection.getStreams().size());
                break;
            case CHANNEL:
                collectionSimplifiedDto.setChannels(collection.getStreams().size());

                break;
        }
        return collectionSimplifiedDto;
    }

    /*collection for insert*/

    public Collection convertToEntity(CollectionInsertDto input) {
        Collection collection = new Collection();
        collection.setId(input.getId());
        collection.setName(input.getName());
        collection.setType(input.getType());

        switch (collection.getType()) {
            case SERIES:
                collection.setVods(input.getSeries() != null ? input.getSeries().stream().map(Series::new).collect(toSet()) : null);
                break;
            case MOVIE:
                collection.setVods(input.getMovies() != null ? input.getMovies().stream().map(Movie::new).collect(toSet()) : null);
                break;
            case RADIO:
                collection.setStreams(input.getRadios() != null ? input.getRadios().stream().map(Radio::new).collect(toSet()) : null);
                break;
            case CHANNEL:
                collection.setStreams(input.getChannels() != null ? input.getChannels().stream().map(Channel::new).collect(toSet()) : null);
                break;
        }
        return collection;
    }


    public CollectionDto convertToDto(Collection collection) {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collection.getId());
        collectionDto.setName(collection.getName());
        collectionDto.setType(collection.getType());

        switch (collection.getType()) {
            case SERIES:
                var series = collection.getVods() != null ? collection.getVods().stream().map(vod -> new MediaPair<>(vod.getId(), vod.getName())).collect(toSet()) : null;
                collectionDto.setSeries(series);
                break;
            case MOVIE:
                var movies = collection.getVods() != null ? collection.getVods().stream().map(vod -> new MediaPair<>(vod.getId(), vod.getName())).collect(toSet()) : null;
                collectionDto.setMovies(movies);
                break;
            case RADIO:
                var radios = collection.getStreams() != null ? collection.getStreams().stream().map(stream -> new MediaPair<>(stream.getId(), stream.getName())).collect(toSet()) : null;
                collectionDto.setRadios(radios);
                break;
            case CHANNEL:
                var channels = collection.getStreams() != null ? collection.getStreams().stream().map(stream -> new MediaPair<>(stream.getId(), stream.getName())).collect(toSet()) : null;
                collectionDto.setChannels(channels);
                break;
        }
        return collectionDto;
    }

}
