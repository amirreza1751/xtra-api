package com.xtra.api.projection.line;

import lombok.Data;

import java.util.List;

@Data
public class LineSecurityView {
    private List<String> allowedIps;
    private List<String> blockedIps;
}
