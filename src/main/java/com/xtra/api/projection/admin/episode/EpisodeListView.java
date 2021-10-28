package com.xtra.api.projection.admin.episode;

import com.xtra.api.projection.admin.video.VideoInfoView;
import lombok.Data;

import java.util.List;

@Data
public class EpisodeListView {
    private Long id;
    private String episodeName;
    private String seriesName;
    private int seasonNumber;
    private String link;
    private List<String> servers;
    private List<VideoInfoView> targetVideos;
}
