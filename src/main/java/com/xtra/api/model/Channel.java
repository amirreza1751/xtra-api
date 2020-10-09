package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Channel extends Stream {

    @JsonIgnore
    @ManyToMany(mappedBy = "channels")
    private List<Collection> collections;

    public Channel() {
        setStreamType(StreamType.CHANNEL);
    }

    public Channel(Long id){
        this();
        setId(id);
    }
}
