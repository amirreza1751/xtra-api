package com.xtra.api.projection.admin.video;

import com.xtra.api.model.vod.EncodeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerEncodeStatus {
    private String server;
    private EncodeStatus encodeStatus;
}
