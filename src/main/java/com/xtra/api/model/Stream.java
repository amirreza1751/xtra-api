package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Parameter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stream_seq")
    @GenericGenerator(
            name = "stream_seq",
            strategy = "enhanced-sequence",
            parameters = {
                    @Parameter(name = "prefer_sequence_per_entity", value = "true"),
                    @Parameter(name = "increment_size", value = "1") })
    private Long id;

    @NotNull
    @Size(min = 5, message = "The Name must be at least 5 characters")
    @Column(unique = true)
    private String name;
    private String logo;
    private StreamType streamType;
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

    @JsonManagedReference
    @OneToOne(mappedBy = "stream", cascade = CascadeType.ALL)
    private StreamInfo streamInfo;

    @JsonManagedReference
    @OneToOne(mappedBy = "stream", cascade = CascadeType.ALL)
    private ProgressInfo progressInfo;

    @ManyToOne
    private Category category;

    @ManyToOne
    private TranscodeProfile transcodeProfile;
    private String customFFMPEG;

    @ManyToMany(cascade = CascadeType.MERGE)
    private List<Server> servers;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<StreamInput> streamInputs;


    //Timestamps
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
