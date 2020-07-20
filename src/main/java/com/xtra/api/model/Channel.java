package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Channel extends Stream {
    public Channel() {
        setStreamType(StreamType.CHANNEL);
    }

    @Override
    public void setStreamInputs(List<StreamInput> streamInputs) {
        super.setStreamInputs(streamInputs);
        if (!streamInputs.isEmpty())
            setCurrentInput(streamInputs.get(0));
    }
}
