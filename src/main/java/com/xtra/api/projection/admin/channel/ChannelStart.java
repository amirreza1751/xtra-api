package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.stream.AdvancedStreamOptions;
import lombok.Data;

import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelStart {

    private Long id;
    private String name;
    private String streamToken;
    private String streamInput;

    //advanced stream options
    private AdvancedStreamOptions advancedStreamOptions;
}
