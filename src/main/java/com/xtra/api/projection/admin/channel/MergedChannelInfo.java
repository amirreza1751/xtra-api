package com.xtra.api.projection.admin.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.ProgressInfo;
import com.xtra.api.model.StreamInfo;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@NoArgsConstructor
public class MergedChannelInfo {

    private String serverName;
    private String uptime;
    private String currentInput;
    private String resolution;
    private String videoCodec;
    private String audioCodec;
    private String speed;
    private String frameRate;
    private String bitrate;

    public MergedChannelInfo(StreamInfo streamInfo, ProgressInfo progressInfo) {
        if (streamInfo != null) {
            uptime = streamInfo.getUptime();
            currentInput = streamInfo.getCurrentInput();
            resolution = streamInfo.getResolution();
            videoCodec = streamInfo.getVideoCodec();
            audioCodec = streamInfo.getAudioCodec();
        }
        if (progressInfo != null) {
            speed = progressInfo.getSpeed();
            frameRate = progressInfo.getFrameRate();
            bitrate = progressInfo.getBitrate();
        }
    }
}
