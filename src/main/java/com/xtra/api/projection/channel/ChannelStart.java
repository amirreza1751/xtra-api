package com.xtra.api.projection.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamInput;
import com.xtra.api.model.StreamServer;
import com.xtra.api.model.StreamType;
import lombok.Data;

import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelStart {

    private Long id;
    private String name;
    private String streamToken;

    private List<String> streamInputs;
    private List<StreamServer> streamServers;
}
