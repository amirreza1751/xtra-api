package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Stream {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private boolean readNative;
    private boolean streamAll;
    private boolean directSource;
    private boolean genTimestamps;
    private String customFFMPEG;

    @ManyToMany
    private List<Server> servers;

}
