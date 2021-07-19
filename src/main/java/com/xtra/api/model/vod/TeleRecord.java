package com.xtra.api.model.vod;

import com.xtra.api.model.stream.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
public class TeleRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private ZonedDateTime start;
    private ZonedDateTime stop;
    private Duration duration;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Video video;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Channel channel;
}
