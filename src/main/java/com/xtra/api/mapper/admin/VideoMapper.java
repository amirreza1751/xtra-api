package com.xtra.api.mapper.admin;

import com.xtra.api.model.vod.Audio;
import com.xtra.api.model.vod.Subtitle;
import com.xtra.api.model.vod.VideoInfo;
import com.xtra.api.projection.admin.video.AudioDetails;
import com.xtra.api.projection.admin.video.SubtitleDetails;
import com.xtra.api.projection.admin.video.VideoInfoView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class VideoMapper {
    public abstract Audio toAudio(AudioDetails audioDetails);

    public abstract Subtitle toSubtitle(SubtitleDetails subtitleDetails);

    public abstract AudioDetails toAudioDetails(Audio audio);

    public abstract SubtitleDetails toSubtitleDetails(Subtitle subtitle);

    public abstract VideoInfoView toVideoInfoView(VideoInfo videoInfo);

    @Mapping(target = "id", ignore = true)
    public abstract VideoInfo toVideoInfo(VideoInfoView videoInfoView);
}
