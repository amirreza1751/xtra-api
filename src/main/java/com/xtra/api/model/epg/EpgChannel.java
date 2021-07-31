package com.xtra.api.model.epg;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.stream.Stream;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString(exclude = {"epgFile"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"name", "language","epg_file_id"})
)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpgChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    private String name;

    @EqualsAndHashCode.Include
    @Column(length = 2)
    private String language;
    private String icon;
    private String url;

    @EqualsAndHashCode.Include
    @Column(name = "epg_file_id", insertable = false, updatable = false)
    private Long epgFileId;

    @ManyToOne
    @JoinColumn(name = "epg_file_id")
    private EpgFile epgFile;

    @OneToMany(mappedBy = "epgChannel", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<EpgProgram> epgPrograms;

    @OneToOne(mappedBy = "epgChannel")
    private Stream stream;

    public EpgChannel(String name, String language, EpgFile epgFile) {
        this.name = name;
        this.language = language;
        this.epgFile = epgFile;
    }

    public boolean addProgram(EpgProgram epgProgram) {
        if (epgPrograms == null)
            epgPrograms = new HashSet<>();
        return epgPrograms.add(epgProgram);
    }

    public boolean removeProgram(EpgProgram epgProgram) {
        if (epgPrograms == null)
            return false;
        return epgPrograms.remove(epgProgram);
    }
    @Override
    public String toString(){
        return this.name;
    }
}
