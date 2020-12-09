package com.xtra.api.projection.line;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.StreamProtocol;
import com.xtra.api.model.User;
import com.xtra.api.projection.downloadlist.DlCollectionView;
import com.xtra.api.projection.downloadlist.DownloadListView;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LineView {
    private Long id;
    private String username;
    private String password;
    private String lineToken;
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isReStreamer = false;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isBanned = false;
    private boolean isIspLocked = false;
    private boolean isStalker;
    private String adminNotes;
    private String resellerNotes;
    private boolean isMag;
    private boolean isEnigma;
    private boolean isMinistraPortal;
    private String mac;

    private List<StreamProtocol> allowedOutputs;
    private User owner;
    private List<DlCollectionView> collections;
}
