package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EpgFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Epg file name must not be empty")
    private String name;
    @NotNull(message = "Epg file source must not be empty")
    private String source;
    private String lastVersionHash;
    private LocalDateTime lastUpdated;

    @NotNull(message = "Epg channels must not be empty")
    @OneToMany(mappedBy = "epgFile", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<EpgChannel> epgChannels;

    public void addChannel(EpgChannel channel) {
        if (epgChannels == null)
            epgChannels = new HashSet<>();
        epgChannels.add(channel);
    }
}
