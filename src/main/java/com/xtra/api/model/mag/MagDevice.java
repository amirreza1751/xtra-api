package com.xtra.api.model.mag;

import com.xtra.api.model.line.Line;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class MagDevice {
    @Id
    private Long id;
    private String mac;
    private String ip;
    private String version;
    private String locale;
    private String deviceId;
    private String deviceId2;
    private String hwVersion;
    private String token;
    private LocalDateTime lastWatchdog;

    @OneToOne(mappedBy = "magDevice")
    private Line line;

    @OneToMany(mappedBy = "magDevice")
    private List<MagEvent> magEvents;

}
