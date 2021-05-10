package com.xtra.api.projection.admin.episode;

import lombok.Data;

import java.util.List;

@Data
public class EpisodeListView {
    private Long id;
    private String episodeName;
    private List<EpisodeServerInfo> serverInfoList;
}
