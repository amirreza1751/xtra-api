package com.xtra.api.model.line;

import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.stream.StreamProtocol;
import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lineUsername;
    private String streamName;
    private String serverName;
    private String ip;
    private String player;
    private String country;
    private LocalDateTime start;
    private LocalDateTime stop;
    private Duration duration;
    private StreamProtocol output;
}
