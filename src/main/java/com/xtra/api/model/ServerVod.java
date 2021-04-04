package com.xtra.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ServerVod {
    @EmbeddedId
    @EqualsAndHashCode.Include
    private ServerVodId id;

    @ManyToOne
    @MapsId("vodId")
    Vod vod;

    @ManyToOne
    @MapsId("serverId")
    Server server;

    public ServerVod() {
    }

    public ServerVod(ServerVodId id) {
        this.id = id;
    }

}
