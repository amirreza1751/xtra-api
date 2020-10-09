package com.xtra.api.mapper;

import com.xtra.api.model.*;
import com.xtra.api.projection.CollectionDto;
import com.xtra.api.projection.CollectionSimpleDto;
import org.mapstruct.Mapper;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public abstract class CollectionMapper {
    /*collection simple display*/

    public abstract CollectionSimpleDto convertToSimpleDto(Collection collection);

    int idsCount(List<Long> ids) {
        return ids.size();
    }


    /*collection with details*/

    public abstract Collection convertToEntity(CollectionDto collectionDto);

    List<Channel> convertIdsToChannels(List<Long> ids) {
        return ids.stream().map(Channel::new).collect(toList());
    }

    List<Movie> convertIdsToMovies(List<Long> ids) {
        return ids.stream().map(Movie::new).collect(toList());
    }

    List<Series> convertIdsToSeries(List<Long> ids) {
        return ids.stream().map(Series::new).collect(toList());
    }

    List<Radio> convertIdsToRadios(List<Long> ids) {
        return ids.stream().map(Radio::new).collect(toList());
    }

    public abstract CollectionDto convertToDto(Collection collection);

    List<Long> convertChannelsToIds(List<Channel> channels) {
        return channels.stream().map(Stream::getId).collect(toList());
    }

    List<Long> convertMoviesToIds(List<Movie> channels) {
        return channels.stream().map(Movie::getId).collect(toList());
    }

    List<Long> convertSeriesToIds(List<Series> channels) {
        return channels.stream().map(Series::getId).collect(toList());
    }

    List<Long> convertRadiosToIds(List<Radio> channels) {
        return channels.stream().map(Stream::getId).collect(toList());
    }
}
