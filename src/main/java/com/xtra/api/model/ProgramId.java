package com.xtra.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@Embeddable
public class ProgramId implements Serializable {
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime stop;
    @Column(length = 2)
    private String language;
    private EpgChannelId epgChannelId;

    public ProgramId(String title, ZonedDateTime start, ZonedDateTime stop, String language) {
        this.title = title;
        this.start = start;
        this.stop = stop;
        this.language = language;
    }
}