package com.xtra.api.model.vod;

import lombok.Getter;

@Getter
public enum VideoCodec {
    H264("h264"),
    H265("h265"),
    VP8("vp8"),
    VP9("vp9"),
    AV1("av1");

    private final String text;

    VideoCodec(String text) {
        this.text = text;
    }
}
