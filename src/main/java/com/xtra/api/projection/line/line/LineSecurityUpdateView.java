package com.xtra.api.projection.line.line;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LineSecurityUpdateView {
    private String password;
    private List<String> allowedIps = new ArrayList<>();
    private List<String> blockedIps = new ArrayList<>();
}
