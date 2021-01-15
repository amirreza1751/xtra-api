package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Embeddable
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Program {
    @EmbeddedId
    private ProgramId id;

    @Lob
    @Column( length = 100000 )
    private String description;
    private String category;


    @ManyToOne()
    @MapsId("epgChannelId")
    @JsonBackReference("epg_channel_id")
    private EpgChannel epgChannel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return id.equals(program.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
