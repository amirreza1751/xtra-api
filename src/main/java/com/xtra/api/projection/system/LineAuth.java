package com.xtra.api.projection.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineAuth {
    private String lineToken;
    private String mediaToken;
    private String ipAddress;
    private String userAgent;
    private String serverToken;
}
