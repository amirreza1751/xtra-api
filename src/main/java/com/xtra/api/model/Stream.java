package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Size(min = 5, message = "The Name must be at least 5 characters")
    @Column(unique = true)
    private String name;
    private String logo;
    private StreamType streamType;
    private String streamToken;
    private boolean readNative = false;
    private boolean streamAll = false;
    private boolean directSource = false;
    private boolean genTimestamps = false;
    private boolean rtmpOutput = false;
    private String userAgent;
    private String notes;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> daysToRestart;
    private LocalTime timeToRestart;


    @ManyToOne
    private TranscodeProfile transcodeProfile;
    private String customFFMPEG;

    @OneToMany(mappedBy = "stream", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<StreamServer> streamServers;


    @OneToMany(mappedBy = "stream", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private Set<CollectionStream> collectionAssigns;

    @ElementCollection
    private List<String> streamInputs;

    @OneToMany(mappedBy = "stream")
    private List<Connection> connections;

    @OneToOne
    private EpgChannel epgChannel;

    @OneToOne(cascade = CascadeType.MERGE)
    private AdvancedStreamOptions advancedStreamOptions;

    private int currentConnections = 0;


    public int getCurrentConnections() {
        return connections.size();
    }

    //Timestamps
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    public Stream(Long id) {
        this.id = id;
    }


    public void addCollection(CollectionStream collectionStream) {
        if (collectionAssigns == null) collectionAssigns = new HashSet<>();
        collectionAssigns.add(collectionStream);
    }
}
