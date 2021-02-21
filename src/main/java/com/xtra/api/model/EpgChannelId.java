package com.xtra.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class EpgChannelId implements Serializable {
    private String name;
    private Long epgId;
    @Column(length = 2)
    private String language;

    public EpgChannelId(String name, String language) {
        this.name = name;
        this.language = language;
    }
}
