package com.xtra.api.projection.admin;

import lombok.Data;

@Data
public class PermissionView {
    private String name;
    private String description;

    public PermissionView(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public PermissionView() {
    }
}
