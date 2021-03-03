package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpgChannel {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private EpgChannelId id;
    private String icon;
    private String url;


    @ManyToOne(cascade = CascadeType.ALL)
    @MapsId("epgId")
    private EpgFile epgFile;


    @OneToMany(mappedBy = "epgChannel", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<Program> programs;

    @OneToOne(mappedBy = "epgChannel")
    private Stream stream;

    public EpgChannel(EpgChannelId id) {
        this.id = id;
    }

    public boolean addProgram(Program program) {
        if (programs == null)
            programs = new HashSet<>();
        return programs.add(program);
    }

    public boolean removeProgram(Program program) {
        if (programs == null)
            return false;
        return programs.remove(program);
    }
}
