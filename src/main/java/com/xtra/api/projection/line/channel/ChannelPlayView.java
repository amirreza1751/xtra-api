package com.xtra.api.projection.line.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChannelPlayView {
    private Long id;
    private String name;
    private String logo;
    private String link;
}
