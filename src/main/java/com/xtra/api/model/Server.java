package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

    @OneToMany(mappedBy = "server", cascade = CascadeType.MERGE)
    @JsonManagedReference("server_id")
//    @ToString.Exclude
    private List<StreamServer> streamServers;

    public void addStreamServer(StreamServer streamServer){
        streamServers.add(streamServer);
    }

    @OneToOne(mappedBy = "server")
    @JsonBackReference("server_id")
    private Resource resource;
}
