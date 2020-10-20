package com.xtra.api.projection;

import com.xtra.api.model.StreamProtocol;
import lombok.Data;

import java.time.Period;
import java.util.List;

@Data
public class PackageView {
    private Long id;
    private String name;
    private boolean isTrial;
    private int credits;
    private Period duration;
    private int maxConnections;
    private boolean canRestream;

    private List<StreamProtocol> allowedOutputs;

    private DownloadListView defaultDownloadList;
}
