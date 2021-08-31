package com.xtra.api.projection;

import lombok.Data;

@Data
public class BackupView {
    private long id;
    private String fileName;
    private String fileSize;
    private String date;
}
