package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Name is Required")
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

    @OneToOne(cascade = CascadeType.ALL)
    private StreamInfo streamInfo;

    @ManyToOne
    private Category category;

    @ManyToOne
    private TranscodeProfile transcodeProfile;
    private String customFFMPEG;

    @ManyToMany(cascade = CascadeType.MERGE)
    private List<Server> servers;

    @OneToMany(cascade = CascadeType.ALL)
    private List<StreamInput> streamInputs;


    //Timestamps
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
