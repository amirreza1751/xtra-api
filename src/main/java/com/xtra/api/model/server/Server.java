package com.xtra.api.model.server;

import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.model.vod.VideoServer;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String domainName;
    private String ip;
    private String corePort;
    private String nginxPort;
    private String interfaceName;
    @Column(unique = true)
    private String token;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "server", cascade = CascadeType.MERGE)
    @ToString.Exclude
    private List<StreamServer> streamServers;

    @OneToOne(cascade = CascadeType.MERGE)
    private Resource resource;

    @OneToMany( mappedBy = "server", cascade = CascadeType.MERGE, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @ToString.Exclude
    private List<VideoServer> videoServers;
}
