package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private boolean readNative = false;
    private boolean streamAll = false;
    private boolean directSource = false;
    private boolean genTimestamps = false;
    private boolean rtmpOutput = false;

    @ManyToOne
    private TranscodeProfile transcodeProfile;
    private String customFFMPEG;

    @ManyToMany
    private List<Server> servers;

    @OneToMany
    private List<StreamInput> streamInputs;

}
