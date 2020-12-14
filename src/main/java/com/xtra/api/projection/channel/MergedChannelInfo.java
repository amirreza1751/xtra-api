package com.xtra.api.projection.channel;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.ProgressInfo;
import com.xtra.api.model.StreamInfo;
import lombok.Data;



@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MergedChannelInfo {

    private String serverName;
    private StreamInfo streamInfo;
    private ProgressInfo progressInfo;


    public MergedChannelInfo(StreamInfo streamInfo, ProgressInfo progressInfo) {
        this.streamInfo = streamInfo;
        this.progressInfo = progressInfo;
    }

    public MergedChannelInfo(){}
}
