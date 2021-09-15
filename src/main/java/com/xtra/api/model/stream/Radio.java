package com.xtra.api.model.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Radio extends Stream {
    public Radio() {
        setStreamType(StreamType.RADIO);
    }
}
