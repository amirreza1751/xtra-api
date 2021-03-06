package com.xtra.api.model.line;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockedIp {
    @Id
    private String ip;
    private LocalDateTime until;
    private Boolean forever = false;

    public BlockedIp(String ip) {
        this.ip = ip;
    }

    public BlockedIp(String ip, Boolean forever) {
        this.ip = ip;
        this.forever = forever;
    }
}
