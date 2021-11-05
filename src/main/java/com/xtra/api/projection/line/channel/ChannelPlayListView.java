package com.xtra.api.projection.line.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChannelPlayListView {
    private Long id;
    private String name;
    private String logo;

    private Set<Long> collections;
    private Set<Long> categories;
    private String link;
}
