package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamServer;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelStart {

    private Long id;
    private String name;
    private String streamToken;
    private int selectedSource;

    private List<String> streamInputs;

    //advanced stream options
    private Map<String, String> inputKeyValues;
    private Map<String, String> outputKeyValues;
    private Set<String> inputFlags;
    private Set<String> outputFlags;
}
