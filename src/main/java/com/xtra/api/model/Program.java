package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Embeddable
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Program {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private ProgramId id;

    @Column(columnDefinition = "TEXT")
    private String description;
    private String category;

    @ManyToOne
    @MapsId("epgChannelId")
    @JsonBackReference("epg_channel_id")
    private EpgChannel epgChannel;

}
