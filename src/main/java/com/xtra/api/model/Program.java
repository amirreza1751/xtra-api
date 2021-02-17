package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Embeddable
public class Program {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private ProgramId id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @MapsId("epgChannelId")
    private EpgChannel epgChannel;

    public Program(ProgramId id) {
        this.id = id;
    }
}
