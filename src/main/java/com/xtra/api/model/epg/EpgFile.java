package com.xtra.api.model.epg;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class EpgFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String source;
    private String lastVersionHash;
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "epgFile", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<EpgChannel> epgChannels;

    public void addChannel(EpgChannel channel) {
        if (epgChannels == null)
            epgChannels = new HashSet<>();
        epgChannels.add(channel);
    }
    @Override
    public String toString(){
        return this.name;
    }
}
