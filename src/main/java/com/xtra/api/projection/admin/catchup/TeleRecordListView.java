package com.xtra.api.projection.admin.catchup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeleRecordListView {
    private Long id;

    private String title;
    private ZonedDateTime start;
    private ZonedDateTime stop;
    private Duration duration;
    private String channelName;
    private String serverName;
}
