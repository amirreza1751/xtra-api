package com.xtra.api.model.stream;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdvancedStreamOptions {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Boolean generatePts;
    private Boolean nativeFrames;
    private Boolean streamAllCodecs;
    private Boolean allowRecording;
    private Boolean outputRTMP;
    private Boolean directSource;

    private String customChannelSID;
    private String onDemandProbeSize;
    private String minuteDelay;
    private String userAgent;
    private String httpProxy;
    private String cookie;
    private String headers;
    private String transcodingProfile;

}
