package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

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

    private String adminNotes;
    private String resellerNotes;
    private String userAgent;
    @ManyToOne
    private Category category;

    @ManyToOne
    private TranscodeProfile transcodeProfile;
    private String customFFMPEG;

    @ManyToMany(cascade = CascadeType.MERGE)
    private List<Server> servers;

    @OneToMany(cascade = CascadeType.ALL)
    private List<StreamInput> streamInputs;

    @OneToOne
    private StreamInput currentInput;

    //Timestamps
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;

}
