package com.xtra.api.projection.reseller.line;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xtra.api.model.StreamProtocol;
import com.xtra.api.projection.admin.BaseView;
import com.xtra.api.projection.admin.downloadlist.DlCollectionView;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LineView implements BaseView {
    private Long id;
    private String username;
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isBanned = false;
    private String resellerNotes;

    private List<StreamProtocol> allowedOutputs;
    private List<DlCollectionView> collections;

    private boolean isCountryLocked = false;
    private String forcedCountry;

    private List<String> allowedIps;
    private List<String> blockedIps;

    private List<String> allowedUserAgents;
    private List<String> blockedUserAgents;
}
