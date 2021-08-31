package com.xtra.api.model.epg;

import com.xtra.api.model.vod.ProgramId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Embeddable
public class EpgProgram {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private ProgramId id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @MapsId("epgChannelId")
    private EpgChannel epgChannel;

    public EpgProgram(ProgramId id) {
        this.id = id;
    }
}
