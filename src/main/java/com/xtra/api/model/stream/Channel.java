package com.xtra.api.model.stream;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.stream.StreamType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Channel extends Stream {

    public Channel() {
        setStreamType(StreamType.CHANNEL);
    }

    public Channel(Long id){
        this();
        setId(id);
    }
}
