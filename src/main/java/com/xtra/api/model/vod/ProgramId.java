package com.xtra.api.model.vod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProgramId implements Serializable {
    private String title;
    private ZonedDateTime start;
    private ZonedDateTime stop;
    private Long epgChannelId;
}
