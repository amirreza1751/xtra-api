package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class AdvancedStreamOptions {
    @Id
    private Long id;

    private Boolean generatePts = false;
    private Boolean nativeFrames = false;
    private Boolean streamAllCodecs = false;
    private Boolean allowRecording = false;
    private Boolean outputRTMP = false;
    private Boolean directSource = false;

    private String customChannelSID;
    private String onDemandProbeSize;
    private String minuteDelay;
    private String userAgent;
    private String httpProxy;
    private String cookie;
    private String headers;
    private String transcodingProfile;

}
