package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Data
@Embeddable
public class ProgramId implements Serializable {
    private String title;
    private String start;
    private String stop;
    private String language;
    private Long epgChannelId;

    public ProgramId (){}

    public ProgramId(String title, String start, String stop, String language, Long epgChannelId) {
        this.title = title;
        this.start = start;
        this.stop = stop;
        this.language = language;
        this.epgChannelId = epgChannelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgramId programId = (ProgramId) o;
        return Objects.equals(title, programId.title) && Objects.equals(start, programId.start) && Objects.equals(stop, programId.stop) && Objects.equals(language, programId.language) && Objects.equals(epgChannelId, programId.epgChannelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, start, stop, language, epgChannelId);
    }
}
