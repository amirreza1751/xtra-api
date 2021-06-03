package com.xtra.api.projection.line.line;

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
    private LocalDateTime expireDate;
    private boolean neverExpire = false;
    private int maxConnections = 1;
    private int currentConnections = 0;
    private boolean isTrial;
    private boolean isBlocked = false;
    private boolean isBanned = false;
}
