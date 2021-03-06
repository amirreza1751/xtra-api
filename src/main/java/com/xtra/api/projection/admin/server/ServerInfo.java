package com.xtra.api.projection.admin.server;

import com.xtra.api.projection.admin.server.resource.ResourceView;
import lombok.Data;

@Data
public class ServerInfo {
    private Long id;
    private String name;
    private String domainName;
    private String ip;
    private String corePort;
    private String nginxPort;
    private String interfaceName;
    private ResourceView resource;
}
