package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.ProgressInfo;
import com.xtra.api.model.StreamInfo;
import com.xtra.api.model.StreamType;
import lombok.Data;

import java.util.List;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChannelInfo {

    private Long id;
    private String name;
    private String logo;
    private StreamType streamType;
    private String streamToken;

    private List<MergedChannelInfo> infos;
}
