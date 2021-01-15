package com.xtra.api.model;

import lombok.Data;

@Data
public class File {
    private String name;
    private String path;
    private long size;
    private boolean isDirectory;
    public File(String name, String path, long size, boolean isDirectory){
        this.name = name;
        this.path = path;
        this.size = size;
        this.isDirectory = isDirectory;
    }
}
