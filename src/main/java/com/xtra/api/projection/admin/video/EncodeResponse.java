package com.xtra.api.projection.admin.video;

import com.xtra.api.model.vod.EncodeStatus;
import lombok.Data;

import java.util.List;

@Data
public class EncodeResponse {
    EncodeStatus encodeStatus;
    List<VideoInfoView> targetVideoInfos;
}
