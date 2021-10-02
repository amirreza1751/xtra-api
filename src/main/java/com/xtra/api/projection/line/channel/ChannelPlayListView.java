package com.xtra.api.projection.line.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelPlayListView {
    private Long id;
    private String name;
    private String logo;

    private Set<Long> collections;
    private Set<Long> categories;
    private String link;
}
