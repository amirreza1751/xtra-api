package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.CollectionDto;
import com.xtra.api.projection.CollectionSimplifiedDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {
    /*collection simple display*/

    @Mapping(target = "channels", expression = "java( collection.getChannels().size() )")
    @Mapping(target = "movies", expression = "java( collection.getMovies().size() )")
    @Mapping(target = "series", expression = "java( collection.getSeries().size() )")
    @Mapping(target = "radios", expression = "java( collection.getRadios().size() )")
    public abstract CollectionSimplifiedDto convertToSimpleDto(Collection collection);



    /*collection with details*/

    public abstract Collection convertToEntity(CollectionDto collectionDto);

    List<Channel> convertIdsToChannels(List<MediaPair<Long, String>> ids) {
        return ids.stream().map(id -> new Channel(id.getId())).collect(toList());
    }

    List<Movie> convertIdsToMovies(List<MediaPair<Long, String>> ids) {
        return ids.stream().map(id -> new Movie(id.getId())).collect(toList());
    }

    List<Series> convertIdsToSeries(List<MediaPair<Long, String>> ids) {
        return ids.stream().map(id -> new Series(id.getId())).collect(toList());
    }

    List<Radio> convertIdsToRadios(List<MediaPair<Long, String>> ids) {
        return ids.stream().map(id -> new Radio(id.getId())).collect(toList());
    }

    public abstract CollectionDto convertToDto(Collection collection);

    List<MediaPair<Long, String>> convertChannelsToIds(List<Channel> channels) {
        return channels.stream().map(channel -> new MediaPair<>(channel.getId(), channel.getName())).collect(toList());
    }

    List<MediaPair<Long, String>> convertMoviesToIds(List<Movie> channels) {
        return channels.stream().map(movie -> new MediaPair<>(movie.getId(), movie.getName())).collect(toList());
    }

    List<MediaPair<Long, String>> convertSeriesToIds(List<Series> seriesList) {
        return seriesList.stream().map(series -> new MediaPair<>(series.getId(), series.getName())).collect(toList());
    }

    List<MediaPair<Long, String>> convertRadiosToIds(List<Radio> radios) {
        return radios.stream().map(radio -> new MediaPair<>(radio.getId(), radio.getName())).collect(toList());
    }
}
