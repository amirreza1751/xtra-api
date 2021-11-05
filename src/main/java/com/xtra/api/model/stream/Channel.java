package com.xtra.api.model.stream;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Channel extends Stream {

    public Channel() {
        setStreamType(StreamType.CHANNEL);
    }

    public Channel(Long id){
        this();
        setId(id);
    }
}
