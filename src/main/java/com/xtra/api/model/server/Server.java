package com.xtra.api.model.server;

import com.xtra.api.model.stream.StreamServer;
import lombok.Data;
import lombok.ToString;

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
    private String token;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "server", cascade = CascadeType.MERGE)
    @ToString.Exclude
    private List<StreamServer> streamServers;

    @OneToOne(cascade = CascadeType.MERGE)
    private Resource resource;
}
