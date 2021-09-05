package com.xtra.api.projection.system;

import com.xtra.api.model.vod.EncodeStatus;
import lombok.Data;

@Data
public class VodStatusView {
    private EncodeStatus status;
    private String location;
}
