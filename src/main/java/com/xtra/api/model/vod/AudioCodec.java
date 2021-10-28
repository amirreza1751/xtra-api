package com.xtra.api.model.vod;

import lombok.Getter;

@Getter
public enum AudioCodec {
    AAC("aac"),
    MP3("mp3"),
    AC3("ac3"),
    EAC3("eac3"),
    VORBIS("vorbis"),
    OPUS("opus"),
    FLAC("flac"),
    DTS("dts");
    private final String text;

    AudioCodec(String text) {
        this.text = text;
    }
}
