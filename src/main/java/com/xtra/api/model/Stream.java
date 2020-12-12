package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
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
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Category category;

    @ManyToOne
    private TranscodeProfile transcodeProfile;
    private String customFFMPEG;

    @JsonIgnore
    @JsonManagedReference("stream_server")
    @OneToMany(mappedBy = "stream", cascade = {CascadeType.MERGE, CascadeType.MERGE})
    private Set<StreamServer> streamServers;


    @OneToMany(mappedBy = "stream", cascade = {CascadeType.MERGE, CascadeType.MERGE})
    private Set<CollectionStream> collectionAssigns;

    @ElementCollection
    private List<String> streamInputs;

    @OneToMany(mappedBy = "stream")
    @JsonManagedReference("stream_id")
    private List<LineActivity> lineActivities;

    private int currentConnections = 0;


    public int getCurrentConnections() {
        return lineActivities.size();
    }

    //Timestamps
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    public Stream(Long id) {
        this.id = id;
    }

    public Stream() {

    }

    public void updateRelationIds() {
        if (streamServers != null) {
            setStreamServers(getStreamServers().stream().peek(streamServer -> {
                        streamServer.setId(new StreamServerId(getId(), streamServer.getServer().getId()));
                        streamServer.setStream(this);
                    }
            ).collect(Collectors.toSet()));
        }

        if (getCollectionAssigns() != null) {
            setCollectionAssigns(getCollectionAssigns().stream().peek(collectionStream -> {
                collectionStream.setId(new CollectionStreamId(collectionStream.getCollection().getId(), getId()));
                collectionStream.setStream(this);
            }).collect(Collectors.toSet()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stream stream = (Stream) o;
        return Objects.equal(id, stream.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addCollection(CollectionStream collectionStream) {
        if (collectionAssigns == null) collectionAssigns = new HashSet<>();
        collectionAssigns.add(collectionStream);
    }
}
